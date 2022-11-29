## SWOZO example infra setup

This setup deploys the system to AWS, note that its recommended to setup it on GCP due to increased orchestrator reliability and performance. AWS was chosen only because of GCP free tier limitations.

- frontend is served as a static website from s3 bucket
- web-app-backend and its database are running on same EC2 instance on docker
- orchestrator and its database are running or another EC2 instance, also on docker

### By default there is no replication, redundancy, monitoring etc.

---

Software images are available publically on docker hub at:

- flok3n/swozo-web-app-backend
- flok3n/swozo-orchestrator

You can also build them using Dockerfiles in respective project directories, see `infra/{backend,orchestrator}/refresh-docker-repo.sh` for reference.

# How to deploy

## Web-app-frontend

You'll need to setup AWS S3 bucket with public access and static page hosting, then configure AWS cli. Once that's done run `infra/frontend/deploy-prod.sh`.

## Web-app-backend

You'll need at least one unix-based VM with SSH. You will also need to install ansible (via pip), then:

1. add ssh private key to `infra/swozo-web.pem` (it will be used by ansible)
2. create `infra/secrets.yml` with filled values from `infra/secrets-example.yml`, see `web-app-backend/README.md` for reference
3. add `gcp-service-key.json` to `infra/backend`, you can take it from GCP platform, see README for reference
4. specify ip address of `[web-app-backends]` in `infra/inventory`, these will be your server instances, to support more than one you should slightly modify db config and run db on another machine
5. run `ansible-playbook backend/prepare-and-run-server.yml`

For health checking `/actuator/health` endpoint can be used.

After software changes run `backend/refresh-docker-repo.sh`, then rerun ansible command.

## Orchestrator

Similarly as above, but in step `3.` add `orchestrator-key.json` file (this can be same `gcp-service-key.json` as for backend) to `infra/orchestrator-key.json`. You'll also need to setup ssh private key for ansible process used to run services on GCP machines, refer to `orchestrator/README.md`.

Then run `ansible-playbook ochestrator/prepare-and-run-orchestrator.yml`

After software changes run `ochestrator/refresh-docker-repo.sh`, then rerun ansible command.

---

For meaning of env variables used to run containers refer to READMEs of projects.
