                  HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS

                                     --------------------

Author and owner:
       Prof. Dr. Loc Nguyen, Loc Nguyen's Academic Network, Vietnam

   
--------------------
Hudup product is the recommender framework dedicated to scientists and software developers who create or deploy recommendation solutions and algorithms in e-commerce and e-learning.

Hudup application has five main modules such as Evaluator, Server, Listener, Balancer and one additional module Toolkit. Note that the Server, Listener and Balancer service ports are 10151, 10152 and 10154, respectively. Toolkit module is used to configure, create and modify datasets.

There are 4 Hudup installer packages bundled with Java runtime:
- Package "Hudup-${version}-windows-jre1.7.exe" is Windows 32 bit installer. This is the most common one.
- Package "Hudup-${version}-windows-x64-jre1.7.exe" is Windows 64 bit installer.
- Package "Hudup-${version}-unix-jre1.7.sh" is Unix/Linux 32 bit installer.
- Package "Hudup-${version}-macos-jre1.7.dmg" is Mac OS installer download JRE automatically.

After installing Hudup in your computer by executing appropriate installer package, you start Hudup application and there are three experimental scripts:

1. Running access point Evaluator. You press "Load script" button and open "batch-sample.script" file at current directory. All sample datasets in folder "datasets" at current directory.

2. Running access point Server, which in turn, running access point Evaluator so as to deploy Hudup in client-server environment. Server automatically remind you to set up Server configuration, for default, you only press "Next" button and finish set up. Note that you must select "rmi_server_query" algorithm in Evaluator in order to evaluate Hudup in client-server environment. Administrator is "admin" with password "admin".

3. Running access point Server, which in turn, running access point Listener or Balancer, which in turn, running access point Evaluator so as to deploy Hudup in client-listener-server environment. You must select "socket_server_query" algorithm in Evaluator in order to evaluate Hudup in client-listener-server environment. 


Hudup software also provides 10 developing packages:
- datasets.zip: contains movielens datasets
- hudup-${version}.zip: contains hudup.jar including five modules Evaluator, Server, Listener, Balancer and Toolkit.
- hudup-${version}-all-in-one.zip: contains whole Hudup product both code and source.
- hudup-${version}-core.zip: contains core classes
- hudup-${version}-doc.zip: contains Hudup document and API
- hudup-${version}-evaluator.zip: contains hudup-evaluator.jar which is Evaluator module
- hudup-${version}-listener.zip: contains hudup-listener.jar which is Listener module
- hudup-${version}-server.zip: contains hudup-server.jar which is Server module
- hudup-${version}-toolkit.zip: contains hudup-toolkit.jar which is Toolkit module
- hudup-runtime-lib.jar: contains libraries Hudup framework used

When you uncompress hudup-${version}-*.zip, there are a respective hudup-*.jar module and shell command files used to execute such jar module. You should copy hudup-runtime-lib.jar to the same directory with such jar module for running successfully. You can copy these jar modules to your application's library directory in order to develop new software.

Thank you for enjoying product.
Best regards,

Mr. Loc Nguyen,
Independent Scholar,
Homepage: www.locnguyen.net,
Email: ng_phloc@yahoo.com,
Tel: 84-975250362

---
P/S: Current version of JRE and JDK is 1.8.0. Current version of Hudup is ${version}. The last built date is March 30, 2020.

	   
--------------------
Acknowledgements:
This product is the place to acknowledge Sir Vu, Ngoc-Dong who gave me valuable comments and advices. These comments help me to improve this product. We also thank following scientists and organizations who gave us software libraries used in Hudup. Some libraries are no longer used in current version of Hudup.

Andy Khan provided JXL library for processing Excel file available at http://jexcelapi.sourceforge.net.

Apache Software Foundation (https://apache.org) provided Apache Common Logging, Apache Commons IO, Apache Commons Math, Apache Logging Services (log4j), Derby JDBC, and XML Commons.

ECMA International provided JSON library for Java, available at http://www.json.org.

Fabio Gagliardi Cozman - University of Sao Paulo provided JavaBayes for building and evaluating Bayesian network, available at https://goo.gl/KvrZsg.

GitHub provided Flexible XML framework for Java available at https://dom4j.github.io.

Google LLC provided GSON library for processing JSON format and Google Core Libraries for Java.

Java Community Process (JCP) Program provided Java Specification Requests (JSRs) for Java annotation.

Java CSV Team provided Java library for processing CSV files available at https://goo.gl/fXJZvH.

JBoss provided Javassist (Java Programming Assistant) to make Java bytecode manipulation simple, available at https://goo.gl/ncSmbS.

JGraph Ltd provided the graph visualization software JGraph available at https://github.com/jgraph.

John DeRegnaucourt provided Java library for JSON format available at https://goo.gl/c9k6N4.

JPF Team provided Java Plug-in Framework (JPF) Project available at http://jpf.sourceforge.net.

Kent Beck, Erich Gamma, David Saff, and Mike Clark are developers of JUnit - A unit testing framework for the Java programming language.

Michael Thomas Flanagan - University College London provided statistic software package "Java Scientific Library" available at https://goo.gl/oX5mvU.

MySQL Team provided MySQL JDBC driver available at https://goo.gl/LpDX5V.

National Distance Education University provided Elvira project for building and evaluating Bayesian network available at https://goo.gl/oEhXgS.

National Institute of Standards and Technology (NIST) provided Java Matrix Package (JAMA) available at https://goo.gl/4Kj9p3.

OneMind provided Java scripted page (Jxp) which is a script-processor that processes JSP-like files, available at http://jxp.sourceforge.net.

Quality Open Software Company provided the Simple Logging Facade for Java (SLF4J) as a simple facade of logging frameworks, available at https://www.slf4j.org.

QuickTable Team provided trial version of QuickTable component which is a Java database grid control, available at http://quicktablejava.appspot.com.

Sam Hocevar provided Reflections package which is a Java runtime metadata analysis which allows users to scan and collection metadata about Java classes and packages in Java projects. Reflections library is available at https://goo.gl/Lf9Evr.

Someones and some organizations on Internet who provide images, icons, source snippets.

Steve Waldman provided mchange-commons-java package for JDBC connection pool available at https://goo.gl/1VAYre.

