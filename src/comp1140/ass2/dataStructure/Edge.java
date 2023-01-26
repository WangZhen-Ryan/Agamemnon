package comp1140.ass2.dataStructure;

/**
 * Edges on the board (in State), which encodes the type and the two node it connects
 * (c) XinTong (Tony) Yan (u6966927@ANU) 2019 All right reserved
 */
public class  Edge {
    public final EdgeType edgeType;
    public int nodeIDA;
    public int nodeIDB;

    /**
     * Construct Edge using my data Structure
     * (c) XinTong (Tony) Yan (u6966927@ANU) 2019 All right reserved
     */
    public Edge(EdgeType edgeType, int nodeIDA, int nodeIDB){
        this.edgeType = edgeType;
        this.nodeIDA = nodeIDA;
        this.nodeIDB = nodeIDB;
    }

    /**
     * Construct Edge from the normal String for compatibility
     * @param edgeStatus String (with 5 chars) representing the Edge
     * (c) XinTong (Tony) Yan (u6966927@ANU) 2019 All right reserved
     */
    public Edge(String edgeStatus){
        edgeType = charToEdgeType(edgeStatus.charAt(0));
        nodeIDA = (edgeStatus.charAt(1) - 48) * 10 + (edgeStatus.charAt(2) - 48);
        nodeIDB = (edgeStatus.charAt(3) - 48) * 10 + (edgeStatus.charAt(4) - 48);
    }

    /**
     * @return the normal encoding of a Edge
     * (c) XinTong (Tony) Yan (u6966927@ANU) 2019 All right reserved
     */
    @Override
    public String toString() {
        String nodeIDAStr = String.valueOf(nodeIDA / 10) + nodeIDA % 10;
        String nodeIDBStr = String.valueOf(nodeIDB / 10) + nodeIDB % 10;
        if (nodeIDA < nodeIDB)
            return edgeType.compatibleChar() + nodeIDAStr + nodeIDBStr;
        else
            return edgeType.compatibleChar() + nodeIDBStr + nodeIDAStr;
    }

    public boolean isEquals (Edge other){
        return this.edgeType == other.edgeType && this.isThisDuplicate(other);
    }

    /**
     * @param c char representing the EdgeType
     * @return the enum EdgeType
     * (c) XinTong (Tony) Yan (u6966927@ANU) 2019 All right reserved
     */
    public static EdgeType charToEdgeType (char c){
        switch (c){
            case 'S': return EdgeType.STRENGTH;
            case 'L': return EdgeType.LEADERSHIP;
            case 'F': return EdgeType.FORCE;
            case 'E': return EdgeType.EMPTY;
            case 'A': return EdgeType.A_LOOM;
            case 'B': return EdgeType.B_LOOM;
            case 'C': return EdgeType.C_LOOM;
            case 'D': return EdgeType.D_LOOM;
        }
        throw new IllegalArgumentException("Potentially bad input, (not one of from S,L,F,E,A,B,C,D)");
    }

    /**
     * Given two node IDs, checks if this Edge is the one making that connection
     * @param checkA  first node ID of the connection desired
     * @param checkB second node ID of the connection desired
     * @return true iff the Edge makes the connection desired
     * (c) XinTong (Tony) Yan (u6966927@ANU) 2019 All right reserved
     */
    public boolean isThisTheOne(int checkA, int checkB) {
        return      (checkA == this.nodeIDA && checkB == this.nodeIDB)
                ||  (checkA == this.nodeIDB && checkB == this.nodeIDA);
    }

    /**
     * Checks if the other edge is a duplicate
     * @return true if it is a duplicate
     * (c) XinTong (Tony) Yan (u6966927@ANU) 2019 All right reserved
     */
    public boolean isThisDuplicate(Edge other){
        return      (other.nodeIDA == this.nodeIDA && other.nodeIDB == this.nodeIDB)
                ||  (other.nodeIDB == this.nodeIDA && other.nodeIDA == this.nodeIDB);
    }


    /**
     * @param nodeID to be check
     * @return true if this Edge contains the nodeID
     * (c) XinTong (Tony) Yan (u6966927@ANU) 2019 All right reserved
     */
    public boolean contains(int nodeID){
        return (nodeID == nodeIDA || nodeID == nodeIDB);
    }

    /**
     * @param nodeID to be checked
     * @return the other ID of the edge
     * (c) XinTong (Tony) Yan (u6966927@ANU) 2019 All right reserved
     */
    public int theOtherID(int nodeID){
        if      (nodeID == nodeIDA) { return nodeIDB; }
        else if (nodeID == nodeIDB) { return nodeIDA; }
        else { throw new RuntimeException("nodeID given not connected by this Edge");}
    }

}
