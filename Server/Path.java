package Server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Stack;

public class Path<T> {

    Collection<Node<Index>> optPath;
    int distance;

    Path(){
        optPath = new ArrayList<>();
        distance = 0;
    }
     Path(Stack<Node<Index>> list){
        distance = 0;
        optPath = new ArrayList<>();
        this.buildPath(list);
    }

    public int getDistance(){ return this.distance;}

    public Collection<Node<Index>> getPath(){return this.optPath;}

    public void printPath(){
        optPath.forEach(node ->{
            System.out.print(node);
        });
    }

    public void buildPath(Stack<Node<Index>> list) {
        if (list.empty())
            return;
        // Extract top of the stack

        Node<Index> x = new Node<Index>(new Index(list.peek().getData().row, list.peek().getData().column),list.peek().getParent(),list.peek().getValue()) ;
        int temp = list.peek().getValue();
        // Pop the top element
        list.pop();

        // Print the current top
        // of the stack i.e., x
        optPath.add(x);
        //System.out.print(temp);
        distance += temp;

        // Proceed to print
        // remaining stack
        buildPath(list);

        // Push the element back
        list.push(x);
    }
    public Collection<Index> getIndexPath(){
        List<Index> indexPath = new ArrayList<>();
        optPath.forEach(node ->{
            indexPath.add(node.getData());
        });
        return indexPath;
    }



//    @Override
//    public String toString() {
//        return optPath.toString();
//    }

}
