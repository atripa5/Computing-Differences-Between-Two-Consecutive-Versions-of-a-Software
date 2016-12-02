# Computing-Differences-Between-Two-Consecutive-Versions-of-a-Software

To run the project:
1. Go to src/main/java/Apietst.java (needed to change the class but didn't get time to change it.)
2. Run the Java program.
3. You will be asked to enter the path of two versions of .udb projects.
4. The program will compute the dependency graph, call graph and isomorphisms between both the versions of The chosen projects.
5. I chose Ad Block Plus (Versions 2.4 and 2.4.1) whose .udb files are present in the Apps folder.

Dependency Graph:
-To create the dependency graph, I have fetched the list of all entities(methods, classes and functions) using ents() of UNDERSTAND API.
-Each of these has been added as a vertex of a graph.
-I found all the entities referenced by each entity using refs() of UNDERSTAND API.
-An edge was added between the referenced entities using JGRAPHT.
-After creating each edge, the complete graph was checked for loops.
-If at any step, a loop was found (A->B, B->A), a duplicate vertext A' was created and the edge was created from A->B and B->A' in order to avoid loops.

Call Graph:
-To create a call graph, I fetched the list of all entities(methods, classes and functions) using ents() of UNDERSTAND API.
-Each of these has been added as a vertex of a graph.
-I found all the entities which were called by another class or a method using refs() with parameter 'Called By' of UNDERSTAND API.
-An edge was added between the caller and callee relations using JGRAPHT.
-After creating each edge, the complete graph was checked for loops.
-If at any step, a loop was found (A->B, B->A), a duplicate vertext A' was created and the edge was created from A->B and B->A' in order to avoid loops.

Isomorphism:
-To check for Isomorphism:
	-I passed both version of dependency and call graphs as a parameter to the function VF2SubgraphIsomorphismInspector<>
	-If isomorphishmExists() gives true result, we print the result.


Tranistive Closure:
-To create the transitive closure, I created a SimpleDirectedGraph and removed any loops after checking if any cycle exists in the graph.
- I checked with Cycledetector() function and it gives false suggesting no loops are present but if I run TransitiveClosure class, I get an error that "loops not allowed."  Hence, I've kept the transitive closure part commented.
