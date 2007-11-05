package datascript.instance;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import datascript.runtime.array.ObjectArray;

import lines.LineGeometries;
import lines.LineGeometry;
import lines.Offset;

public class LineGeometriesInstanceHandler implements DataScriptInstanceHandler
{
    private static final int GEOMETRIES = 1;
    private static final int NUM_POINTS = 3;
    private static final int OFFSETS = 5;
    private static final int X = 7;
    private static final int Y = 8;
    private static final int DX = 10;
    private static final int DY = 11;

    private List<Line> lines;
    
    private Point currentPoint;
    private Point lastPoint;
    private Line currentLine;
    
    class Point
    {
        int x;
        int y;
    }
    
    class Line extends ArrayList<Point>
    {
        Line(int numPoints)
        {
            super(numPoints);
        }
    }
    
    
    
    @Override
    public void bigIntegerField(int fieldId, BigInteger value)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void endArray(int fieldId)
    {
        switch (fieldId)
        {
            case OFFSETS:
                lines.add(currentLine);
        }
    }

    @Override
    public void endArrayElement(int fieldId)
    {
    }

    @Override
    public void endCompound(int fieldId)
    {
    }
    
    @Override
    public void endInstance(int typeId)
    {
    }

    @Override
    public void enumField(int fieldId, int value)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void integerField(int fieldId, long value)
    {
        if (lines == null)
        {
            lines = new ArrayList<Line>((int)value);
            return;
        }
        
        switch (fieldId)
        {
            case NUM_POINTS:
                currentLine = new Line((int) value);
                break;
                
            case X:
                currentPoint = new Point();
                currentPoint.x = (int) value;
                break;
                
            case Y:
                currentPoint.y = (int) value;
                lastPoint = currentPoint;
                currentLine.add(currentPoint);
                break;
                
            case DX:
                currentPoint = new Point();
                currentPoint.x = (int) value + lastPoint.x;
                break;
                
            case DY:
                currentPoint.y = (int) value + lastPoint.y;
                lastPoint = currentPoint;
                currentLine.add(currentPoint);
                break;
                
                
        }
        
    }

    @Override
    public void startArray(int fieldId)
    {
    }

    @Override
    public void startArrayElement(int fieldId)
    {
    }

    @Override
    public void startCompound(int fieldId)
    {
    }

    @Override
    public void startInstance(int typeId)
    {
    }

    @Override
    public void stringField(int fieldId, String value)
    {
        // TODO Auto-generated method stub

    }
    
    public List<Line> decode(LineGeometries geometries)
    {
        ArrayList<Line> lines = new ArrayList<Line>();
        for (int i = 0; i < geometries.getNumGeometries(); i++)
        {
            LineGeometry geometry = geometries.getGeometries().elementAt(i);
            int numPoints = geometry.getNumPoints();
            Line line = new Line(numPoints+1);
            Point point = new Point();
            point.x = geometry.getAnchor().getX();
            point.y = geometry.getAnchor().getY();
            line.add(point);
            Point lastPoint = point;
            ObjectArray<Offset> oa = geometry.getOffsets();
            for (int j = 0; j < numPoints; j++)
            {
                Offset offset = oa.elementAt(j);
                point = new Point();
                point.x = lastPoint.x + offset.getDx();
                point.y = lastPoint.y + offset.getDy();
                line.add(point);
                lastPoint = point;
            }
            lines.add(line);
        }
        return lines;
    }

}
