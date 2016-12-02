package org.jgrapht.graph.builder;

//package org.jgrapht.alg.isomorphism;

import com.scitools.understand.*;
import org.jgrapht.GraphMapping;
import org.jgrapht.alg.CycleDetector;
import org.jgrapht.alg.TransitiveClosure;
import org.jgrapht.alg.isomorphism.VF2SubgraphIsomorphismInspector;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.graph.SimpleGraph;

import java.util.Iterator;
import java.util.*;
import java.lang.*;

//import org.junit.*;


public class Apietst {


    // A method for creating Dependency Graphs
    public static DefaultDirectedGraph Create_DependencyGraph(String str){

            DefaultDirectedGraph <String, DefaultEdge> dependGraph = new DefaultDirectedGraph<String, DefaultEdge>(
                DefaultEdge.class);

            CycleDetector<String, DefaultEdge> cycl = new CycleDetector<>(dependGraph);

            try{

           //Opening the understand database
            Database db = Understand.open(str);
            Entity [] entities = db.ents("Method, Class, Function");  // Fetching the list of all the entities.

            for(Entity e : entities){
                dependGraph.addVertex(e.name()); //All the entities are added as a vertex for the dependency graph.
            }

            // Loop to Check all the entities referenced by an entity and drawing an edge between them showing dependency.
            for(Entity e: entities){
                Reference [] Callrefs= e.refs("Depends On","Method, Class, Function",true);
                for (Reference cRef: Callrefs){
                    Entity cEnt= cRef.ent();
                    dependGraph.addEdge(e.name(),cEnt.name());

                    //Checking for cycles in the graph to avoid loops in order to calculate transitive closure.
                    if(cycl.detectCycles()){

                        dependGraph.removeEdge(e.name(),cEnt.name());
                        String ver = cEnt.name()+"'";
                        dependGraph.addVertex(ver);
                        dependGraph.addEdge(e.name(),ver);
                     }
            }}

        }catch (UnderstandException e){
            System.out.println("Failed opening Database:" + e.getMessage());
            System.exit(0);
        }

        return dependGraph;  //Returning the created dependency graph
    }

    // A method to create Call Graph

    public static DefaultDirectedGraph Create_CallGraph(String str){

        DefaultDirectedGraph <String, DefaultEdge> callGraph = new DefaultDirectedGraph<String, DefaultEdge>(
                DefaultEdge.class);
        CycleDetector<String, DefaultEdge> cycl = new CycleDetector<>(callGraph);
        try{

                 Database db = Understand.open(str);  //Opening the understand database.


            Entity [] entities = db.ents("Class, Method, Function");  //Fetching a list of all the classes, methods and functions

            for(Entity e : entities){
                callGraph.addVertex(e.name());  //Adding all classes and methods as a vertex for call graph
            }

            for(Entity e: entities){

                //A block to add edges between classes and methods called by the chosen entity
                Reference [] Callrefs= e.refs("Called By","Method, Class",true);
                for (Reference cRef: Callrefs){
                    Entity cEnt= cRef.ent();
                    callGraph.addEdge(e.name(),cEnt.name());
                    if(cycl.detectCycles()){
                        callGraph.removeEdge(e.name(),cEnt.name());
                        String ver = cEnt.name()+"'";
                        callGraph.addVertex(ver);
                        callGraph.addEdge(e.name(),ver);
                    }}
                                }
        }catch (UnderstandException e){
            System.out.println("Failed opening Database:" + e.getMessage());
            System.exit(0);
        }
        return callGraph;
    }

/*
    public static SimpleDirectedGraph Create_SimpleGraph(String str){

        SimpleDirectedGraph <String, DefaultEdge> dependGraph = new SimpleDirectedGraph<String, DefaultEdge>(
                DefaultEdge.class);

        CycleDetector<String, DefaultEdge> cycl = new CycleDetector<>(dependGraph);

        try{


            //Opening the understand database
            Database db = Understand.open(str);
            Entity [] entities = db.ents("Method, Class, Function");  // Fetching the list of all the entities.

            for(Entity e : entities){

                dependGraph.addVertex(e.name()); //All the entities are added as a vertex for the dependency graph.

            }

            // Loop to Check all the entities referenced by an entity and drawing an edge between them showing dependency.
            for(Entity e: entities){

                Reference [] Callrefs= e.refs("Depends On","Method, Class, Function",true);
                for (Reference cRef: Callrefs){
                    Entity cEnt= cRef.ent();
                    dependGraph.addEdge(e.name(),cEnt.name());

                    if(cycl.detectCycles()){

                        dependGraph.removeEdge(e.name(),cEnt.name());
                        String ver = cEnt.name()+"'";
                        dependGraph.addVertex(ver);
                        dependGraph.addEdge(e.name(),ver);
                    }
                }
            }
            System.out.println(cycl.detectCycles());
        }catch (UnderstandException e){
            System.out.println("Failed opening Database:" + e.getMessage());
            System.exit(0);
        }
        return dependGraph;  //Returning the created dependency graph
    }
    */



    public static void main(String[] args) {

        // Fetching the paths of two consecutive versions of the app by the user.
        System.out.println("Enter the path for .udb file of version 1 of the app");
        Scanner scanner= new Scanner(System.in);
        String projPath1= scanner.next();
        System.out.println("Enter the path for the .udb file of version 2 of the app");
        String projPath2=scanner.next();

        //   String projPath1 = "Apps\\Adblockplus1.udb";
        //  String projPath2 = "Apps\\Adblockplus2.udb";

        //Creating the dependency graph of version 1
        DefaultDirectedGraph DependencyGraph_ver1 = new DefaultDirectedGraph(DefaultEdge.class);
        DependencyGraph_ver1= Create_DependencyGraph(projPath1);


        //Creating the call graph for version 1
        DefaultDirectedGraph CallGraph_ver1 = new DefaultDirectedGraph(DefaultEdge.class);
        CallGraph_ver1 = Create_CallGraph(projPath1);


        //Creating the dependency graph of version 2
        DefaultDirectedGraph DependencyGraph_ver2 = new DefaultDirectedGraph(DefaultEdge.class);
        DependencyGraph_ver1= Create_DependencyGraph(projPath2);


        //Creating the call graph for version 2
        DefaultDirectedGraph CallGraph_ver2 = new DefaultDirectedGraph(DefaultEdge.class);
        CallGraph_ver1 = Create_CallGraph(projPath2);


        //Checking for isomorphic case
        VF2SubgraphIsomorphismInspector<String, DefaultEdge> dependency_isomorphism = new VF2SubgraphIsomorphismInspector<String, DefaultEdge>(DependencyGraph_ver1,DependencyGraph_ver2);
        VF2SubgraphIsomorphismInspector<String, DefaultEdge> call_isomorphism = new VF2SubgraphIsomorphismInspector<String, DefaultEdge>(CallGraph_ver1,CallGraph_ver2);

        Iterator<GraphMapping<String, DefaultEdge>> iter = dependency_isomorphism.getMappings();
        Iterator<GraphMapping<String, DefaultEdge>> itr = call_isomorphism.getMappings();

        //Printing isomorphism of dependency graphs
        System.out.println("Printing the isomorphic mappings for Dependency Graphs");
        if(dependency_isomorphism.isomorphismExists())
        {
        while (iter.hasNext())
        {
            System.out.println(iter.next());
        }}

        else {
            System.out.println("No isomorphism case found");
        }

        //Printing isomorphism for Call Graphs
        System.out.println("Printing the isomorphic mappings for Call Graphs");
        if(call_isomorphism.isomorphismExists())
        {
            while (itr.hasNext())
            {
                System.out.println(itr.next());
            }}

        else {
            System.out.println("No isomorphism case found");
        }


        //For transitive closure:
/*
        SimpleDirectedGraph trans = new SimpleDirectedGraph<String , DefaultEdge>(DefaultEdge.class);
        trans = Create_SimpleGraph(projPath1);

        TransitiveClosure.INSTANCE.closeSimpleDirectedGraph (trans);

        Iterator itrr = trans.edgeSet().iterator();

        while(itrr.hasNext())
        {
            System.out.println(itrr.next().toString());
        }

        System.out.println(DependencyGraph_ver1.edgeSet().size()); */


    }
}
