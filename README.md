# ResearchExchange

## Project Configuration

Our frontend server does not take any special configuration to deploy; as long as the machine has a v12 or newer Node.js installation and installs all the dependencies listed in `package.json` using `npm i`, the frontend server will run just by calling `npm run build` followed by `npm run start`.
 
The backend server/API, however, requires special configuration relevant to Oracle Cloud deployment. In order for the application to access any of the OCI resources, the user needs to create a local configuration file, `~/.oci/config`, in which the contents of the file are provided by creating an API key from your User Settings in Oracle Cloud.

Additionally, we must obtain the OCID from the following Oracle Cloud resources used by the application:
 
- Oracle Autonomous Database
- Identity Compartment
- Vault
 
With the local configuration completed, additional configuration files within the project are needed in order for the application to access the Oracle Autonomous Database from Oracle Cloud. The following YAML files are presented with the required path and configuration:
 
- `application-oraclecloud.yml`
  - Path: `src/main/resources/`
  - Configuration: `datasources.default.ocid: <ocid-autonomous-database>`
- `bootstrap-oraclecloud.yml`
  - Path: `src/main/resources/`
  - Configuration:
    ````
    oci:
      config.profile: DEFAULT
      vault:
        config.enabled: true
        vaults:
          ocid: <ocid-vault>
          compartment-ocid: <ocid-compartment>
    ````
 
In addition to the configurations above, the following gradle.build dependencies are required for the YAML files to access OCI:

- `implementation("io.micronaut.oraclecloud:micronaut-oraclecloud-atp")`
- `implementation("io.micronaut.oraclecloud:micronaut-oraclecloud-sdk")`
- `implementation("io.micronaut.oraclecloud:micronaut-oraclecloud-vault")`

## Running the Application

To start the frontend server, navigate to `frontend` and call `npm run build` and `npm run start`. To start the backend server, navigate to `backend/research_exchange` and run `MICRONAUT_ENVIRONMENTS=oraclecloud ./gradlew run`. The backend server can be accessed through `https://localhost:8080` and the frontend is hosted at `https://localhost:3000`.
