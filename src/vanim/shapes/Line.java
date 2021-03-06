package vanim.shapes;

import vanim.root.builder.LineBuilder;
import vanim.root.vobjects.VObject;
import vanim.storage.color.Subcolor;
import vanim.storage.vector.FVector;

import static vanim.util.Mapper.map3;
import static vanim.util.map.MapEase.EASE_IN_OUT;


public class Line extends VObject {

    float weight;
    FVector start, end, average;
    int strokeNum;
    float dividend = 100;

    public Line(LineBuilder builder) { //p not used here...
        super(builder);
        weight = builder.getLineWeight();
        start = builder.getStart();
        end = builder.getEnd();
        average = new FVector((start.getX() + end.getX()) / 2.0f, (start.getY() + end.getY()) / 2.0f);
    }

    public Line setEnd(float x, float y) {
        end.setXY(x, y);
        recalculateDimensions();
        return this;
    }

    public Line setStartEnd(FVector start, FVector end) {
        this.start = start;
        this.end = end;
        return this;
    }

    public Line setStrokeCap(int WHAT) {
        strokeNum = WHAT;
        return this;
    }

    public Line setMapPower(float jj) {
        mapPower = jj;
        return this;
    }

    public Line setStart(float x, float y) {
        start.setXY(x, y);
        recalculateDimensions();
        return this;
    }

    public FVector getDirectionVec() {
        return FVector.subtract(end, start).normalize();
    }

    public float getLength() {
        return FVector.subtract(end, start).getMag();
    }

    protected void recalculateDimensions() {
        dimensions.setXY(end.getX() - start.getX(), end.getY() - start.getY());
        dimensions.setZ(end.getZ() - start.getZ());
    }

    public Line setDividend(float dividend) {
        this.dividend = dividend;
        return this;
    }

    public FVector getAverage() {
        return average;
    }

    public FVector getStart() {
        return start;
    }

    public FVector getEnd() {
        return end;
    }

    //float value, float start1, float stop1, float start2, float stop2, int type, int when
    protected void push(float dividend) { //TODO: fix param dividend
        float intermediary = map3(incrementor++, 0, dividend, 0, 1, mapPower, EASE_IN_OUT);
        pos.setX((start.getX() + intermediary * (end.getX() - start.getX())));
        pos.setY(start.getY() + intermediary * (end.getY() - start.getY()));

        if (incrementor >= dividend) {
            incrementor = (long) dividend;
        }
    }

    @Override
    public boolean display(Object... obj) {
        canvas.strokeCap(strokeNum);
        push(dividend / 2);
        if (weight != 0)
            canvas.strokeWeight(weight);

        if (color.getHue().is255())
            canvas.stroke(color.getHue(), new Subcolor(), color.getBrightness(), color.getAlpha());
        else
            canvas.stroke(color);

        canvas.line(FVector.scale(start, geometricSpace.getScale()), FVector.scale(pos, geometricSpace.getScale()));
        return pos.equals(end);
    }
}