package comp1140.ass2.gui.dataStructure;

/**
 *  This is for recording Vector (Locations/coordinates)
 *  plus some simple but useful operations on them.
 */
public class Vector {
    public double x;
    public double y;

    public Vector(double x, double y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return "(" + this.x + ", " + this.y + ")";
    }

    public boolean equals(Vector other) {
        return other.x == this.x && other.y == this.y;
    }

    public void add(Vector other){
        this.x += other.x;
        this.y += other.y;
    }
    public double magnitude(){
        return Math.sqrt(x * x + y * y);
    }

//    public double dot(Vector other){
//        return other.x * this.x + other.y * this.y;
//    }

//    public double angle(Vector other) {
//        return Math.acos(this.dot(other) / (this.magnitude() + other.magnitude()));
//    }

    public Vector midPoint(Vector other){
        return new Vector((this.x + other.x) * 0.5, (this.y + other.y) * 0.5);
    }

    /**
     * This is vecA, input vecB return vecA to vecB
     * @param other the other vector
     * @return from this vector to the other vector
     */
    public Vector relative(Vector other){
        return new Vector( this.x - other.x, this.y - other.y);
    }

//    public void scale(double scalar){
//        this.x *= scalar;
//        this.y *= scalar;
//    }

    public double distance(Vector other) {
        return this.relative(other).magnitude();
    }

//    public double angle;

//    public void calculateAngle(){
//        this.angle = Math.atan(this.y / this.x);
//    }

//    public void extendBy(double length){
//        calculateAngle();
//        this.x += length * Math.cos(this.angle);
//        this.y += length * Math.sin(this.angle);
//    }

}
