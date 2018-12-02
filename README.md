# File Storage

Dokerized Spirng Boot application providing RESTful API for storing files and images. 
User uploads files, images or images referenced by URL. The application stores all uploaded resources into internal 
storage (specific directory on server).
When image stored into the storage, the thumbnail representation (100x100 px) of the image is stored along the image automatically.   
The storage directory should be specified as parameter for starting application.  
The REST API consists of a single endpoint: 
```
localhost:8080/upload-files
```
There are two content types are supported: application/json and multipart/form-data

## Getting Started

### Prerequisites
To build and run application you need to have installed:
1. Git
1. JDK 8
1. Gradle 5 (recommended, but not required)
1. Docker (if you going to run the application in docker container)

Instead of install gradle on your machine you can use gradlew script that build the application 

### Installing
1. Clone the repository from GitHub
    ```
    git clone https://github.com/RiderToha/file-storage
    ```
2. Build the project
    Build with gradle
    ```
    gradle bootJar
    ```
    Or with gradlew if gradle is not installed    
    ```
    gradlew bootJar
    ```
### Running locally
To start application locally use the command (use existing directory path for file-storage.path):
``` 
java -Dfile-storage.path=C:\Temp -jar build/libs/file-storage-1.0.0.jar
```

### Deploying on docker container
To deploy on linux container use the following commands:

On Windows (PowerShell):
```
$env:FILE_STORAGE_PATH="C:\Temp";docker-compose up --build
```
On Linux:
```
FILE_STORAGE_PATH=/tmp docker-compose up --build
```
For FILE_STORAGE_PATH parameter use a directory existing in your system

## Usage Examples
Uploading one image by URL, one general file and two image files:
```
curl -F "imageUrls=https://upload.wikimedia.org/wikipedia/commons/thumb/3/3a/Cat03.jpg/1200px-Cat03.jpg" \
-F "files=@/home/user/File1.txt" \
-F "images=@/home/user/Graphic1.jpg" \
-F "images=@/home/user/Graphic2.jpg" \
localhost:8080/upload-files
```

Uploading one file and one image in json format (files content encoded in base64):
```
curl --header "Content-Type: application/json" \
--request POST \
--data '{"files":[{"fileName":"xyz-file", "contentBase64":"zyx"}],"images":[{"fileName":"cat-image.jpg", "contentBase64":"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"}] }' \
http://localhost:8080/upload-files
```


