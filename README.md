# Stepper - Workflow/Pipeline System
Stepper is a flexible workflow/pipeline system that allows the assembly of different scenarios (called flows) from common components (called steps). 
It facilitates the execution of these flows and generation of the desired results. The project encompasses various UI clients (simple CLI, Desktop, Client-server), user and permission management components, concurrent client support, and data collection for progress tracking.

### Technologies Used:
- Java 8
- Client-server architecture (Tomcat)
- Multithreading
- JSON serialization (Gson)
- Desktop application - JavaFX
- Users and permission management
- SQL JDBC
- XML Parser (Jaxb)

### Overview:

This project is a comprehensive system designed for managing stepper engines. It comprises several modules, each serving a distinct purpose to fulfill the requirements of the system.

#### Modules:
1. **Stepper Engine**
    - The `stepper-engine` module holds all the core logic and functionalities of the product. It serves as the backbone of the entire system, providing essential operations and computations for controlling stepper engines (XML parse, run flows, etc.).

2. **Stepper Server App**
    - The `stepper-server-app` module manages all client endpoints. It relies on the logic provided by the `stepper-engine` module, making it dependent on its functionalities to address client requests effectively.

3. **Stepper Client**
    - The `stepper-client` module represents the user interface for interacting with the system as a client. It provides a user-friendly interface for users to access and utilize the features offered by the system.

4. **Stepper Admin Client**
    - The `stepper-admin-client` module serves as the administrative interface for managing users and loading steps via XML. It offers administrative functionalities to oversee and control various aspects of the system's operation.

5. **DTO (Data Transfer Object)**
    - The `dto` module facilitates the exchange of data between the server and clients. It defines structured objects used for server responses and client requests, ensuring seamless communication and data consistency throughout the system.

6. **Stepper UI (Cmd)**
    - The `stepper-ui` module provides a command-line interface (CLI) for running the application as a single instance without a server-client architecture. It offers basic functionalities accessible via the command line. *Please note that this module are no longer actively maintained.*

7. **Stepper UI JavaFX**
    - The `stepper-ui-Javafx` module offers the same functionality as the `stepper-ui` module but provides a JavaFX-based graphical user interface (GUI) for improved user experience. It allows users to interact with the application using a visual interface. *Please note that this module are no longer actively maintained.*

#### Admin-
  ![image](https://github.com/AmitAvital1/stepper/assets/116808245/bec240b8-741f-4f41-968e-fc7e37de1e5d)
  
![image](https://github.com/AmitAvital1/stepper/assets/116808245/879972e3-c6f0-4814-a76a-e78690c4dc42)



#### Client-

  ![image](https://github.com/AmitAvital1/stepper/assets/116808245/7be3c125-0813-49cb-a9e8-a2924c89411b)
  
  ![image](https://github.com/AmitAvital1/stepper/assets/116808245/87d28219-8653-4f09-bf59-ffd158432d3d)

