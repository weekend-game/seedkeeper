## SeedKeeper

### How to run the program

Download the repository to your computer. Everything you need for the program is located in the app folder. Navigate to the app folder and run the program by double-clicking the SeedKeeper.jar file or, if the program doesn't start, double-click the SeedKeeper.bat file. If the program doesn't start, download and install Java 11 or later and repeat the steps above.

### How to open a project in Eclipse

In Eclipse, select "Import..." from the "File" menu. In the window that opens, select "Existing projects into workspace." Navigate to the folder with the downloaded repository and click "Finish." The project will open in Eclipse. In the Package Explorer (on the left side of the screen), double-click the SeedKeeper.java file. The file will open for editing (in the center of the screen). Run the program by pressing Ctrl+F11 or using your preferred method for running programs in Eclipse.
DBMS

### Choosing a DBMS

When choosing a DBMS, I considered the following:

* The DBMS should be able to be embedded into the application. Launching any additional programs before using my application would somewhat detract from the user's enjoyment of viewing their seed collection.
* My program is written in Java, and the DBMS should be written in Java. Of course, you can make anything work with Java, but I don't want to waste time on that.
* I know SQL and would like to use it, not anything else, which means the DBMS should be relational.
* I should use the first DBMS I come across that meets my first three requirements, since my goal is to create a seed tracking program, not to study the current state of the DBMS market.

I chose **Derby**. A large amount of high-quality documentation on this DBMS can be found on the Derby website. But to create a crop tracking application, I needed the following.

**Installing Derby** requires:

* Download the DBMS files and place them in a folder. I'm running Windows 10 and using Java 11, so I chose version 10.15.2.0, which I downloaded and placed in the C:\Programs\Derby\ folder.
* Create the DERBY_HOME system variable with the path to the DBMS. I used C:\Programs\Derby.
* Add %DERBY_HOME%\bin to the PATH system variable.

**Creating and Working with a Database**

* Run a command prompt in the folder where you want to store the database. The repository includes the dos.bat file for this.
* Run the Derby ij utility. To do this, enter: java -jar "%DERBY_HOME%/lib/derbyrun.jar" ij
* Create or connect to the db database. To do this, enter: CONNECT 'jdbc:derby:db;user=user;create=true';
* Run the table creation script. To do this, enter: RUN 'create_tables_1.sql';
* then: RUN 'create_tables_2.sql';
* then: RUN 'create_tables_3.sql';
* You can enter any SQL commands here, and they will be executed by the DBMS.
* Exit the Derby ij utility. To do this, enter: EXIT;

Don't forget to include the ; after each command.

The application's interaction with the database will be based on JDBC. I don't see any point in using an ORM, such as Hibernate, for such a simple application that will use no more than a dozen tables.
