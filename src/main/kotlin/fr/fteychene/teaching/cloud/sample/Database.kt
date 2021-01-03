package fr.fteychene.teaching.cloud.sample

import org.http4k.core.*
import org.http4k.core.body.form
import org.http4k.lens.*
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.template.ViewModel
import java.sql.Connection
import java.sql.DriverManager
import java.util.*

object DbNotActivated : ViewModel

data class TodoView(
    val todos: List<Todo>
) : ViewModel

data class Todo(
    val id: UUID,
    val description: String
)

fun <R> execute(provider: () -> Connection, block: (Connection) -> R): R =
    provider().use(block)

fun postgres(): () -> Connection = {
    DriverManager.getConnection(
        "jdbc:${System.getenv("POSTGRESQL_ADDON_URI")!!}",
        System.getenv("POSTGRESQL_ADDON_USER")!!,
        System.getenv("POSTGRESQL_ADDON_PASSWORD")!!
    )
}

fun database(templates: BiDiBodyLens<ViewModel>): RoutingHttpHandler =
    if ((System.getenv("DB_ACTIVATED") ?: "false").toBoolean())
        routes(
            "/database" bind Method.GET to {
                Response(Status.OK).with(templates of TodoView(getTodos(postgres())))
            },
            "/database" bind Method.POST to {
                createTodo(postgres(), Todo(UUID.randomUUID(), it.form("description")!!))
                Response(Status.OK).with(templates of TodoView(getTodos(postgres())))
            }
        )
    else "/database" bind Method.GET to { Response(Status.OK).with(templates of DbNotActivated) }


fun createTodo(connection: () -> Connection, todo: Todo) =
    execute(connection) {
        it.createStatement()
            .executeUpdate("CREATE TABLE IF NOT EXISTS todos (id UUID, description varchar)")
        val stmt = it.prepareStatement("INSERT INTO todos VALUES (?, ?)")
        stmt.setObject(1, todo.id)
        stmt.setString(2, todo.description)
        stmt.executeUpdate()
    }


fun getTodos(connection: () -> Connection): List<Todo> =
    execute(connection) {
        it.createStatement()
            .executeUpdate("CREATE TABLE IF NOT EXISTS todos (id UUID, description varchar)")
        it.createStatement().executeQuery("SELECT id, description from todos")
            .use { resultSet ->
                generateSequence {
                    if (resultSet.next()) {
                        Todo(UUID.fromString(resultSet.getString(1)), resultSet.getString(2))
                    } else null
                }.toList()
            }

    }

