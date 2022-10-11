# 🔬 ResearchExchange

## Project Configuration 🚧

Our frontend server does not take any special configuration to deploy if you are running the frontend on the same instance as the backend; as long as the machine has a v12 or newer Node.js installation and installs all the dependencies listed in `package.json` using `npm i`, the frontend server will run just by calling `npm run build` followed by `npm run start`. Further instructions are listed further below. 
 
The backend server/API, however, requires special configuration relevant to Oracle Cloud deployment. In order for the application to access any of the OCI resources, the user needs to create a local configuration file, `~/.oci/config`, in which the contents of the file are provided by creating an API key from your User Settings in Oracle Cloud.

Additionally, we must obtain the OCID from the following Oracle Cloud resources used by the application:
 
- Oracle Autonomous Database
- Identity Compartment
- Vault
 
With the local configuration completed, additional configuration files within the project are needed in order for the application to access the Oracle Autonomous Database from Oracle Cloud. The following YAML files are presented with the required path and configuration:
 
File Structure Location: 
```
ResearchExchange
   +- backend/
   |   +- research_exchange/
   |   |   +- ...
   |   |   +- src/
   |   |   |   +- ...
   |   |   |   +- main/
   |   |   |   |   +- ...
   |   |   |   |   +- resources/
   |   |   |   |   |   +- ...
   |   |   |   |   |   +- application-oraclecloud.yml
   |   |   |   |   |   +- bootstrap-oraclecloud.yml
```
 
- `application-oraclecloud.yml`
  - Path: `src/main/resources/`
  - Configuration: `datasources.default.ocid: <ocid-autonomous-database>`
- `bootstrap-oraclecloud.yml`
  - Path: `src/main/resources/`
  - Configuration:
    ```yaml
    oci:
      config.profile: DEFAULT
      vault:
        config.enabled: true
        vaults:
          ocid: <ocid-vault>
          compartment-ocid: <ocid-compartment>
    ```
 
In addition to the configurations above, the following gradle.build dependencies are required for the YAML files to access OCI:

- `implementation("io.micronaut.oraclecloud:micronaut-oraclecloud-atp")`
- `implementation("io.micronaut.oraclecloud:micronaut-oraclecloud-sdk")`
- `implementation("io.micronaut.oraclecloud:micronaut-oraclecloud-vault")`

If you are running the both frontend and backend on one virtual machine, then you may skip this step. Otherwise, line 6 of **`api.ts`** file must modified to have the public IP of the VM instead of `localhost`.  
Line 6 of `api.ts`: `const BASE_URL = "http://localhost:8080";` -> `const BASE_URL = "http://<vm-public-ip>:8080";`

File Structure Location: 
```
ResearchExchange
    +- frontend/
    |   +- ...
    |   +- utils/
    |   |   +- ...
    |   |   +- api.ts
```

## 🖥️ Building Frontend and Backend Environment

### Provisioning Backend Server 

<ins>Note:</ins> The commands below are run on an **Oracle Linux 8** image with the **AMD64** shape. However, you may need modify the commands to fit your distros package manager and [graalvm download](https://github.com/graalvm/graalvm-ce-builds/releases). 

```bash
# Update package manager.
$ sudo yum update -y

# Install git (Other Linux distros may already have git installed.)
$ sudo yum install git -y

# Clone the project
$ git clone https://github.com/TyroneWu547/ResearchExchange.git

# Create empty directory for GraalVM to download in
$ mkdir ~/graalvm

# Download GraalVM to graalmv directory (Modify download link to match your distribution [here](https://github.com/graalvm/graalvm-ce-builds/releases).)
$ wget https://github.com/graalvm/graalvm-ce-builds/releases/download/vm-22.1.0/graalvm-ce-java11-linux-amd64-22.1.0.tar.gz -P ~/graalvm/

# Extract graalvm tar file
$ tar -xzf graalvm/graalvm-ce-java11-linux-amd64-22.1.0.tar.gz -C ~/graalvm/

# Append GraalVM binaries to PATH in .bashrc
$ echo 'export PATH="~/graalvm/graalvm-ce-java11-22.1.0/bin:$PATH"' >> ~/.bashrc

# Append the JAVA_HOME environment variable to the .bashrc file.
$ echo 'export JAVA_HOME=~/graalvm/graalvm-ce-java11-22.1.0' >> ~/.bashrc

# Reload .bashrc settings for PATH and environment variables to update.
$ source ~/.bashrc

# To verify that graalvm has been installed/set correctly, execute: 
$ java -version
openjdk version ...
OpenJDK Runtime Environment GraalVM CE ...
OpenJDK 64-Bit Server VM GraalVM CE ...
```

### Provisioning Frontend Server

```bash
# Curling node version 16.x setup script
$ sudo curl --silent --location https://rpm.nodesource.com/setup_16.x | sudo bash -

# Installing nodejs
$ sudo yum install nodejs -y

# To verify that the correct node version
$ node --version
V16.xx.x

# Change directory to the frontend of the project
$ cd ResearchExchange/frontend/

# Install npm packages
$ npm i

# Build the frontend application
$ npm run build
```

## 🪂 Deploying the Application 

### Backend

```bash
# To run the backend
$ MICRONAUT_ENVIRONMENTS=oraclecloud ~/ResearchExchange/backend/research_exchange/gradlew run
<output>
<...>
<output>
20:02:18.277 [main] INFO  io.micronaut.runtime.Micronaut - Startup completed in 10882ms. Server Running: http://deployment-test:8080
<=========----> 75% EXECUTING
```

Your output may look slightly different. If the loading bar has reached **`75% EXECUTING`**, then the backend is successfully running. 

### Frontend

```bash
# Starting the frontend
$ npm run start
> start
> next start
ready - started server on 0.0.0.0:3000, url: http://localhost:3000
```

The application can how be reached through `http://<frontend-ip>:3000`.

## 😵‍ Common Error

Depending on the distribution you are running on, the built-in firewall may be configured to block any traffic from coming into your virtual machine. The following commands below can be executed to open the port.

Execute on both frontend and backend instance (do not need to run twice if you have both running on the same instance):
```bash
# Disable firewall and setup iptables. 
$ sudo systemctl disable firewalld
$ sudo systemctl stop firewalld
$ yum install iptables-services -y
$ sudo systemctl start iptables
$ sudo systemctl start ip6tables
$ sudo systemctl enable iptables
$ sudo systemctl enable ip6tables
```

Backend:
```bash
# Add port 8080 to iptables.
$ sudo iptables -I INPUT -m state --state NEW -p tcp --dport 8080 -j ACCEPT
```

Frontend: 
```bash
# Add port 3000 to iptables.
$ sudo iptables -I INPUT -m state --state NEW -p tcp --dport 3000 -j ACCEPT
```
