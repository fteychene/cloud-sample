# Cloud-sample


## Deploy

### On Clever cloud

```bash
clever create --type gradle --org orga_e4d64185-94d8-4d10-9d26-31b39dafd743 testapp
clever scale --build-flavor S
clever deploy
```