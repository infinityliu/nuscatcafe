# nuscatcafe
nuscatcafe backend project developed with Scala (Play+Reactive Mongo)
## Setup
We use IntelliJ to run the application code, MongoDB Compass to visualize the content of MongoDB, and Postman to send and test the HTTP methods. So please download these tools, their link are listed below:
- https://www.jetbrains.com/zh-cn/idea/download/#section=windows
- https://www.mongodb.com/try/download/community
- https://www.postman.com/downloads/

## Usage
#### IntelliJ
1. Please pull the code from github by using the Git tool in IntelliJ. You can select **_File -> Settings_**, and search 'Git' to download it for IntelliJ.
2. Find the SBT Command console. Still, you can select **_File -> Settings_**, and search 'sbt' to download it.
3. For the first time, please type in the instruction below in the SBT Command console: `build`. It will automatically download all the tools we mentioned in the build.sbt file.
4. Then please type in: `compile`, `run` to start the service. It will host in localhost 9000 port.

#### MongoDB Compass
Just simply press **Connect** button on the main page, it will use localhost 27017 port and access the database. The collections will be automatically created when dealing with the sending request.

#### Postman
Please import the APIs we generated in the file **nuscatcafe.postman_collection.json**. You can select **_File -> Import_** and choose this file, then all the function would appear on the left. Just simply click them and use. More details will be shown in the presentation video.

## Reference
We list all the tools we used in our project here:
- https://www.playframework.com/documentation/2.8.x/Home
- http://reactivemongo.org/releases/1.0/documentation/
- https://jwt.io/libraries?language=Scala
