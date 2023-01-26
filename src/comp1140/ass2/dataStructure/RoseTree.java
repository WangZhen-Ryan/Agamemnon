package comp1140.ass2.dataStructure;

import comp1140.ass2.Agamemnon;

import java.util.Arrays;
import java.util.List;


/**
 * Overall explicit tree structure for gameTree
 * Cite: https://gist.github.com/jooyunghan/ba5812d8eccb4dd1147f
 * Partly co-designed by (a) Zhen Wang and (b) Jingyang You
 */

public class RoseTree<T> {
    public final T value;
    public final List<RoseTree<T>> children;

    public RoseTree(T value,List<RoseTree<T>> children) {
        this.value = value;
        this.children = children;
    }
    @Override
    public String toString(){
        if (children == null) {
            return value.toString();
        }
        else {
            return value.toString() + children.toString();
        }}


        public int countLeaves() {
            if (children.isEmpty()) {return 1;}
            return children.stream().mapToInt(RoseTree::countLeaves).sum();
        }

    public int height(){
        if (children != null) {
            return 1 + children.stream().mapToInt(RoseTree::height).max().orElse(0);
        }
        else{
            return 0;
    }}

    public int[] score(){
        return Agamemnon.getTotalScore((String[]) value);
    }
    @SafeVarargs
    private static <T> RoseTree<T> rose(T value, RoseTree<T>...children){
        return new RoseTree<>(value, Arrays.asList(children));
    }

    public String[] toStringArray() {
        return (String[]) value;
    }
}
