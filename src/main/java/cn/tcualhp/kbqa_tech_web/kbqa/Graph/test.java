package cn.tcualhp.kbqa_tech_web.kbqa.Graph;

import edu.princeton.cs.algs4.BreadthFirstPaths;
import edu.princeton.cs.algs4.Graph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Stack;

import java.io.File;
import java.util.Iterator;

public class test {

    private String txtPath = "C:\\Users\\mammo\\Desktop\\KBQA\\src\\main\\java\\Neo4jTinyG.txt";

    // 构建neo4j数据库的结构映射图
    Graph neo4j2TinyG() {
        In in = new In(new File(txtPath));
        Graph g = new Graph(in);
        System.out.println(g.toString());
        return g;
    }

    // 广度优先搜索
    Iterable<Integer> BFPshortestPath(Graph g, int start, int end) {
        BreadthFirstPaths bfPath = new BreadthFirstPaths(g, start);
        if (!bfPath.hasPathTo(end)) return null;
        Iterable<Integer> path = bfPath.pathTo(end);
        return path;
    }

    public static void main(String[] args) {
        test t = new test();
        Graph g = t.neo4j2TinyG();
        Iterable<Integer> iterable = t.BFPshortestPath(g, 1, 3);
        Iterator iterator = iterable.iterator();
        while(iterator.hasNext()){
            System.out.println(iterator.next());
        }
    }

}
