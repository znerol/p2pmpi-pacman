P2P-MPI Information can be found at http://wwww.p2pmpi.org 

Installation instructions are in file INSTALL.*

Instructions to build in Eclipse 3.4.2:
======================================

     1. Go to 'File---> New Project' and then, select a Wizard:---> SVN project. After that, create a new repository location
        'svn+ssh://developername@scm.gforge.inria.fr/svn/p2pmpi'.

     2. After this, you have a form 'Enter SSH credentials' where you must choose between 'password authorization' and 
        'private key authorization' and then, choose 'checkout as a project for the workspace'.

     3. After svn checkout, go to Window---->Preferences. You will see a pop-up window heading "Preferences".
        In the list ('General', 'Ant', 'Data Management', .....), select 'Ant' and double click on this.

     4. In the drop down list, select 'Runtime' and click on the 'Classpath' heading.

     5. You will see three entry columns there named 'Ant Home Entries(Default)','Global Entries' and 'Contributed Entries'. 
        Select 'Global Entries' and click on button 'Add External Jars'. You can add both 'log4j.jar' and 'p2pmpi.jar' here.

     6. Now, go to Window ----> Show View ----> Other ----> Ant.

     7. Now a new window "Ant" should appear next to the "Outline" window. Go to it and click on the icon that looks like 
        an ant "Add buildfiles". Select p2pmpi-mp ----> build.xml. Then, click ok. 

     8. Now, Click on the '+' sign to drop down the menu and double click on "Dist (Default)" and this will compile the project.
