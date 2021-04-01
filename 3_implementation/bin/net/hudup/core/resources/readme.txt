HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS

(http://www.locnguyen.net/st/products/hudup)

Author and owner: Prof. Dr. Loc Nguyen, Loc Nguyen's Academic Network, Vietnam
Homepage: www.locnguyen.net
Email: ng_phloc@yahoo.com
Tel: 84-975250362


--------------------
Hudup product is the recommender framework dedicated to scientists and software developers who create or deploy recommendation solutions and algorithms in e-commerce and e-learning. Current version of Hudup is ${version}. The last built date is March 30, 2020. Hudup framework is composed of three systems:
1. The infrastructure to set up recommendation algorithms.
2. The evaluation system to measure recommendation algorithms according to metrics.
3. The simulation environment to execute and test recommendation algorithms before deploying them in real-time applications.

Hudup application has five main modules such as Evaluator, Server, Listener, Balancer and one additional module Toolkit. Note that the Server, Listener and Balancer service ports are 10151, 10152 and 10154, respectively. Toolkit module is used to configure, create and modify datasets.

Hudup is available at http://www.locnguyen.net/st/products/hudup or http://www.hudup.net. Its GitHub source is available at https://github.com/sunflowersoft/hudup-ext. The tutorial is available at https://www.scitepress.org/PublicationsDetail.aspx?ID=3Tk7XB8m0LA=

The easiest way to install Hudup is to download the all-in-one version (hudup-${version}-all-in-one.zip) available at https://drive.google.com/file/d/1XdU2I-yLzJ5uSXzrgehe2LFZMJidm9IJ or http://www.locnguyen.net/st/products/hudup and then to uncompress such version. Hudup runs on standard edition Java with version OpenJDK 15 that you have to download and install before installing Hudup. Finally, you start Hudup application and there are three experimental scripts:

1. Running access point Evaluator (evaluator.bat, evaluator.sh, evaluator-remote.bat, evaluator-remote.sh). You press "Load script" button and open "batch-sample.script" file at current directory. All sample datasets in folder "datasets" at current directory.

2. Running access point Server (server.bat, server.sh), which in turn, running access point Evaluator so as to deploy Hudup in client-server environment. Server automatically remind you to set up server configuration, for default, you only press "Next" button and finish set up. Administrator is "admin" with password "admin". Later on, you run Evaluator to connect Server.

3. Running access point Server, which in turn, running access point Listener (listener.bat, listener.sh) or Balancer (balancer.bat, balancer.sh), which in turn, running access point Evaluator so as to deploy Hudup in client-listener-server environment. You select "socket_server_query" algorithm in Evaluator in order to evaluate Hudup in client-listener-server environment. 

Hudup software also provides 4 developing packages:
- datasets.zip: contains sample datasets.
- hudup-runtime-lib.jar: contains runtime libraries necessary for Hudup framework to run.
- hudup-${version}.zip: contains Hudup framework (hudup.jar) including five modules Evaluator, Server, Listener, Balancer and Toolkit.
- hudup-${version}-all-in-one.zip: contains whole Hudup product including code, source, and runtime libraries.

When you uncompress hudup-${version}.zip or hudup-${version}-all-in-one.zip, there are the respective jar module (hudup.jar), sub-directories, and shell command files used to run Hudup framework. With hudup-${version}.zip, you need to copy hudup-runtime-lib.jar to the same uncompressed directory for running successfully. You can copy the jar module (hudup.jar) to your application's library directory in order to develop new software.

Thank you for enjoying product.
Best regards,


--------------------
Acknowledgements:
This product is the place to acknowledge Sir Vu, Ngoc-Dong who gave me valuable comments and advices. These comments help me to improve this product. We also thank following scientists and organizations who gave us software libraries used in Hudup. Some libraries are no longer used in current version of Hudup.

Andy Khan provided JXL library for processing Excel file available at http://jexcelapi.sourceforge.net.

Apache Software Foundation (https://apache.org) provided Apache Common Logging, Apache Commons IO, Apache Commons Math, Apache Logging Services (log4j), Derby JDBC, and XML Commons.

Christian Schlichtherle provided TrueZip (https://christian-schlichtherle.bitbucket.io/truezip) which is a Java based virtual file system (VFS) which enables client applications to perform CRUD (Create, Read, Update, Delete) operations on archive files. TrueZip is very important to Hudup.

CICYT (a Spanish research agency) provided Elvira (http://www.ia.uned.es/~elvira/index-en.html) which is a Bayesian expert system.

ECMA International provided JSON library for Java, available at http://www.json.org.

Fabio Gagliardi Cozman - University of Sao Paulo provided JavaBayes for building and evaluating Bayesian network, available at https://goo.gl/KvrZsg.

GitHub provided Flexible XML framework for Java available at https://dom4j.github.io.

Google LLC provided GSON library for processing JSON format and Google Core Libraries for Java.

Java Community Process (JCP) Program provided Java Specification Requests (JSRs) for Java annotation.

Java CSV Team provided Java library for processing CSV files available at https://goo.gl/fXJZvH. Java CSV is very important to Hudup.

JBoss provided Javassist (Java Programming Assistant) to make Java bytecode manipulation simple, available at https://goo.gl/ncSmbS.

JGraph Ltd provided the graph visualization software JGraph available at https://github.com/jgraph.

Jos de Jong provided the mathematical expression parser available at http://www.speqmath.com/tutorials/expression_parser_java.

John DeRegnaucourt provided Java library for JSON format available at https://goo.gl/c9k6N4.

JPF Team provided Java Plug-in Framework (JPF) Project available at http://jpf.sourceforge.net.

Kent Beck, Erich Gamma, David Saff, and Mike Clark are developers of JUnit - A unit testing framework for the Java programming language.

Machine Learning Group - The University of Waikato (https://www.cs.waikato.ac.nz/~ml/people.html) provided Weka software (https://www.cs.waikato.ac.nz/~ml/weka) is a collection of machine learning algorithms for data mining tasks.

Michael Kutschke provided Jayes (https://github.com/kutschkem/Jayes) is a Bayesian Network Library for Java.

Michael Thomas Flanagan - University College London provided statistic software package "Java Scientific Library" available at https://goo.gl/oX5mvU.

MySQL Team provided MySQL JDBC driver available at https://goo.gl/LpDX5V.

National Distance Education University provided Elvira project for building and evaluating Bayesian network available at https://goo.gl/oEhXgS.

National Institute of Standards and Technology (NIST) provided Java Matrix Package (JAMA) available at https://goo.gl/4Kj9p3.

OneMind provided Java scripted page (Jxp) which is a script-processor that processes JSP-like files, available at http://jxp.sourceforge.net.

Quality Open Software Company provided the Simple Logging Facade for Java (SLF4J) as a simple facade of logging frameworks, available at https://www.slf4j.org.

QuickTable Team provided trial version of QuickTable component which is a Java database grid control, available at http://quicktablejava.appspot.com.

Sam Hocevar provided Reflections package which is a Java runtime metadata analysis which allows users to scan and collection metadata about Java classes and packages in Java projects. Reflections library is available at https://goo.gl/Lf9Evr. Reflections package is very important to Hudup.

Someones and some organizations on Internet like David Gilbert (https://bit.ly/34qnaI1), Nobuo Tamemasa (https://bit.ly/34qnaI1), Icon Archive (http://www.iconarchive.com), etc who provided images, icons, source snippets.

Steve Waldman provided mchange-commons-java package for JDBC connection pool available at https://goo.gl/1VAYre.

