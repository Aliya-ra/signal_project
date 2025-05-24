# UML Diagram Explanations

## 1. Alert Generation System
This UML diagram models the Alert Generation System in the project. This system is responsible for handling the needed alerts for patients with critical situations. We first check each patient's data and their specific thresholds to decide whether an alert needs to be dispatched or not.
AlertGenerator class is the core of this system, where it receives the data for the patients from the DataStorage class. Then it evaluates the data and triggers an Alert when some critical condition is observed. The Alert class handles the data and details needed for the alerts. Then, the alert is passed to the class AlertManager, where it maps patientIds to specific MedicalStaff and also stores some logs for the alerts.
Patients’ data is getting handled by Patient, PatientRecord, and DatStorage. These classes are responsible for storing and managing the data for the patient, such as heartbeats, etc. HealthDataSimulator is the part of the system that is responsible for generating simulated data. It uses the AlertGenerator class and OutputStrategy interface to do so.
To conclude, the Alert Generation System is using this structure to handle data, condition evaluation, and triggering alerts for patients with critical conditions. I believe using this structure makes the program maintainable, testable, and modular.
 
----------------------------------------------------------------------

## 2. Data Storage System
The data storage system is responsible for providing a secure and precise code base to handle the data storage part. The core in this diagram is the DataStorage class, which is mapping the patient IDs to the incoming data. By using this structure, we tried to separate the data storage part from the rest of the program to keep it modular and maintainable. 
Each object from the class Patient represents an individual that the system is monitoring. Also, the diagram shows the relationship between the Patient class and the PatientRecord class. Each patient record belongs to a patient (composition). In other words, the patient record cannot exist without being assigned to a specific patient through their patient ID.
Additionally, I have the interface DataReader that enhances the maintainability of the program by reducing the coupling between subsystems. It makes it easier to add new methods of reading the data without any need for changing the storage part of the program.
The DataRetriever class is responsible for fetching the stored records when we need them. We want the data to be accessible quickly and easily. I tried to design the structure of the system in a way that keeps it simple, maintainable, and modular.


----------------------------------------------------------------------

## 3. Patient Identification System
The Patient Identification System is responsible for making sure that each incoming data point is correctly mapped to the associated patient in our records. This system is crucial to make the program accurate and avoid mistakes that will arise from mistakes in the data handling. For example, we want to avoid sending the alert to the wrong patient.
The class IdentityManager is the core of this system. It uses another class named PatientIdentifier to validate the patient IDs and checks for any possible mismatch. Here I check that the patient can be found in the hospital data base, so it should be a HospitalPatient. We are keeping a list of verified patients. It is easy to access the data by using the methods in these classes (For example getPatientData() method)
We are linking the classes Patient, PatientRecord, PatientHospital, PatientIdentifier, and IdentityManager to ensure a precise and accurate handling of the patients’ data and syncing it with the hospital data base. For example, each PatientHospital object has a patient ID, medical history, which is a list of PatientRecord, and a map for thresholds to see the danger zone for each data type (for example, heartbeat) for that specific patient.

----------------------------------------------------------------------

## 4. Data Access Layer System
The Data Access layer is responsible for getting the raw data for the patients from all possible sources and prepare it for the next stage processes. In the core of this part of the program, we have the class DataSourceAdapter that work as the pillar. It works with different listeners and listens to them. After receiving new data, it sends the data to the DataParser for the next steps. After parsing, it sends the parsed data to the DataStorage. I tried to keep the design in a way that different logics, such as receiving, parsing, and storing, are separated. 
I used the DataListener interface to define how the incoming data is getting handled. This step has 3 important methods. startListening(), stopListenting() and onDataRecieved(). There are 3 classes that implement this interface. TCPDataListener which listens on TCP, WebSocketListener which listens on WebSocket, and FileDataListener which reads the files. This structure is using different listeners to listen to different specific data sources. 
The DataParser converts the raw strings into a format the system understands and can work with. After the data is parsed, it is passed to the DataStorage class. This class keeps track of all patient records and information. 
