package Server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Stack;
/**
 * This Class is a helper class to store a path -
 * collection of indexes as nodes{@link Node} between two indexes in a matrix and the sum of all the elements in that path as int.
 *
 */
public class Path<T> {

    Collection<Node<Index>> optPath;
    int distance;

    /**
     * Default constructor
     */
    Path(){
        optPath = new ArrayList<>();
        distance = 0;
    }

    /**
     * Path constructor that gets a stack of indexes as nodes{@link Node}
     * and generates a list of nodes and the sum of the elements in that path by using the buildPath method.
     * @param list A stack of indexes as nodes {@link Node}.
     */
     Path(Stack<Node<Index>> list){
        distance = 0;
        optPath = new ArrayList<>();
        this.buildPath(list);
    }

    /**
     * Gets the sum of indexes values in path.
     * @return sum of elements as int
     */
    public int getDistance(){ return this.distance;}

    /**
     * Gets the path.
     * @return Path as collection of nodes{@link Node}
     */
    public Collection<Node<Index>> getPath(){return this.optPath;}

    public void printPath(){
        optPath.forEach(node ->{
            System.out.print(node);
        });
    }

    /**
     * This method builds a list of indexes as nodes{@link Node} and sums their value recursively
     * @param list A stack containing the indexes as nodes to build the path list in class
     */
    public void buildPath(Stack<Node<Index>> list) {
        if (list.empty())
            return;
        Node<Index> x = new Node<Index>(new Index(list.peek().getData().row, list.peek().getData().column),list.peek().getParent(),list.peek().getValue()) ;
        int temp = list.peek().getValue();
        list.pop();
        optPath.add(x);;
        distance += temp;
        buildPath(list);
        list.push(x);
    }

    /**
     * This method generates a collection of indexes{@link Index} from a predefined nodes {@link Node} path list in class.
     * @return A path as a collection of indexes {@link Index}.
     */
    public Collection<Index> getIndexPath(){
        List<Index> indexPath = new ArrayList<>();
        optPath.forEach(node ->{
            indexPath.add(node.getData());
        });
        return indexPath;
    }
}
