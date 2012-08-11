package whirlwind.demo.gui.searchtabs;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.Line2D;
import java.net.URL;
import java.util.TreeMap;
import java.util.Map.Entry;

import javax.swing.ImageIcon;

import org.fuzzydb.attrs.location.EcefVector;
import org.fuzzydb.dto.attributes.Point3DAttribute;
import org.fuzzydb.util.geo.OsgbGridCoord;

import whirlwind.demo.gui.CoordRemap;
import whirlwind.demo.gui.OsgbConv;

import com.wwm.indexer.SearchResult;
import com.wwm.indexer.demo.internal.WhirlwindCommon;
import com.wwm.indexer.demo.internal.WhirlwindQuery;
import com.wwm.indexer.demo.internal.WhirlwindQuery.QueryConfig;


// OS references:
// Lands End 143276,29752
// Margate 626123,159603
// Thurso 309057,967586

// Map places:
//Lands End 805,3359	(41)
//Margate 2042,3000		(400)
//Thurso 1186,810		(2590)

// lands end to Thurso, px = 381,2549
// lands end to Thurso, os = 177995,943305

// os to pix divisor =  467.18, 370.07

// os to pix formula: (x-134245) / 406 + 805


public class MapPane extends SearchTab {

    private static final long serialVersionUID = 1L;

    // OSGB grid refs (Eastings,Northings) for locations around Britain:
    //	private static final float osxThurso = 311560;//309057;
    //	private static final float osyThurso = 968240;//967586;

    //	private static final float osxMargate = 637105;//626123;
    //	private static final float osyMargate = 170740;//159603;

    //	private static final float osxLandsEnd = 134660; // 143276;
    //	private static final float osyLandsEnd = 25025; //29752;

    // Pixel offsets on map image for the same locations:
    //	private static final float pxThurso = 1186;
    //	private static final float pyThurso = 810;

    //	private static final float pxMargate = 2042;
    //	private static final float pyMargate = 3000;

    //	private static final float pxLandsEnd = 805;
    //	private static final float pyLandsEnd = 3359;

    private static final float pixBorder = 20;

    //	private static final float xscale = ((osxMargate-osxLandsEnd) / (pxMargate-pxLandsEnd) + (osxMargate-osxThurso) / (pxMargate-pxThurso) + (osxThurso-osxLandsEnd) / (pxThurso-pxLandsEnd))/3;
    //	private static final float yscale = (osyThurso-osyLandsEnd) / (pyLandsEnd-pyThurso);
    //	private static float xoffset = ((pxLandsEnd * xscale - osxLandsEnd) + (pxThurso * xscale - osxThurso) + (pxMargate * xscale - osxMargate))/3;
    //	private static float yoffset;

    private static float gridxstart = 0;
    private static float gridystart = 0;
    private static float gridxinc = 10000;
    private static float gridyinc = 10000;
    private static int gridxcount = 71;
    private static int gridycount = 101;
    private static int gridsegs = 1;
    private static Color gridColour = Color.DARK_GRAY;
    private static Color gridMinorColour = Color.LIGHT_GRAY;

    private Image map;
    private int mapWidth;
    private int mapHeight;

    private CoordRemap remap = new CoordRemap();

    private class MapCoord {
        public float x;
        public float y;
        public MapCoord(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }

    private TreeMap<String, Color> searchPoints = new TreeMap<String, Color>();
    private TreeMap<String, TreeMap<String, Color>> searchLines = new TreeMap<String, TreeMap<String, Color>>();

    private TreeMap<String, Color> resultsPoints = new TreeMap<String, Color>();
    private TreeMap<String, TreeMap<String, Color>> resultsLines = new TreeMap<String, TreeMap<String, Color>>();


    // This is not called when loaded via XML
    public MapPane() {
        super();
        initialize();
    }

    @Override
    public void whirlwind_initialize(String name, WhirlwindCommon WCommon, WhirlwindQuery query) {
        super.whirlwind_initialize(name, WCommon, query);


        remap.addPoint(134660, 25025, 405, 3359);		// Lands End
        remap.addPoint(401080,91040, 1055, 3182);		// Poole
        remap.addPoint(531480,104400, 1371, 3148);		// Brighton
        remap.addPoint(637105, 170740, 1642, 3000);		// Margate

        remap.addPoint(258480,281760, 690, 2709);		// Aberystwyth
        remap.addPoint(407520,287600, 1076, 2687);		// Birmingham
        remap.addPoint(545120, 258760, 1420, 2767); 	// Cambridge
        remap.addPoint(652600,306360, 1698, 2662);		// GT Yarmouth

        remap.addPoint(330680,436600, 870, 2298);		// Blackpool
        remap.addPoint(429800,433960, 1139, 2306);		// Leeds
        remap.addPoint(517760,467280, 1364, 2222);		// Bridlington

        remap.addPoint(205920,560320, 526, 1981);		// Stranraer
        remap.addPoint(340320,555719, 887, 1985);		// Carlisle
        remap.addPoint(439560,557000, 1153, 1984);		// Sunderland

        remap.addPoint(259160,665679, 658, 1689);		// Glasgow
        remap.addPoint(340400,730680, 878, 1497);		// Dundee

        remap.addPoint(211280,774041, 514, 1385);		// Fort William
        remap.addPoint(394400,806640, 1028, 1285);		// Aberdeen

        remap.addPoint(311560, 968240, 786, 810);		// Thurso

        // --------------------------------------------
        // This should come from XML
        // --------------------------------------------
        searchPoints.put("Location", Color.red);
        searchPoints.put("StartLocation", Color.red);
        searchPoints.put("EndLocation", Color.black);

        TreeMap<String, Color> searchLineEnd = new TreeMap<String, Color>();
        searchLineEnd.put("EndLocation", Color.red);
        searchLines.put("StartLocation", searchLineEnd);


        resultsPoints.put("Location", Color.green);
        resultsPoints.put("StartLocation", Color.green);
        resultsPoints.put("EndLocation", Color.blue);

        TreeMap<String, Color> resultsLineEnd = new TreeMap<String, Color>();
        resultsLineEnd.put("EndLocation", Color.black);
        resultsLines.put("StartLocation", resultsLineEnd);
        // --------------------------------------------

        initialize();

        URL url = this.getClass().getResource("/whirlwind/demo/gui/resource/uk.gif");

        ImageIcon ii = new ImageIcon(url);

        map = ii.getImage();

        mapWidth = map.getWidth(null);
        mapHeight = map.getHeight(null);

        //		yoffset = (mapHeight-pyLandsEnd) * yscale - osyLandsEnd;
    }



    void drawLine(Graphics2D g2, Point3DAttribute ecefstart, Point3DAttribute ecefend, Color color, float xoff, float yoff, float zoom) {
        MapCoord start = EcefToMap(ecefstart);
        MapCoord end = EcefToMap(ecefend);

        float x1 = (start.x + xoff) * zoom;
        float y1 = (start.y + yoff) * zoom;
        float x2 = (end.x + xoff) * zoom;
        float y2 = (end.y + yoff) * zoom;

        g2.setPaint(color);
        g2.draw(new Line2D.Float(x1, y1, x2, y2));
    }

    void drawPoint(Graphics2D g2, Point3DAttribute ecefpoint, Color color, float xoff, float yoff, float zoom) {
        MapCoord point = EcefToMap(ecefpoint);
        float x1 = (point.x + xoff) * zoom;
        float y1 = (point.y + yoff) * zoom;

        g2.setPaint(color);
        g2.fillOval((int)x1-5, (int)y1-5, 10, 10);

    }

    void renderResults(Graphics2D g2, float xoff, float yoff, float zoom) throws Exception {

        for (SearchResult result : getResults()) {
            // -----------------------------------------------------------
            // Render configured lines
            // -----------------------------------------------------------
            for (Entry<String, TreeMap<String, Color>> lineEntry : resultsLines.entrySet()) {
                Point3DAttribute start = (Point3DAttribute) result.getAttributes().get(lineEntry.getKey());
                if (start == null) {
                    continue;
                }
                for (Entry<String, Color> endLineEntry: lineEntry.getValue().entrySet()) {
                    Point3DAttribute end = (Point3DAttribute) result.getAttributes().get(endLineEntry.getKey());
                    if (end == null) {
                        continue;
                    }
                    drawLine(g2, start, end, endLineEntry.getValue(), xoff, yoff, zoom);
                }
            }
            // -----------------------------------------------------------

            // -----------------------------------------------------------
            // Render configured points
            // -----------------------------------------------------------
            for (Entry<String, Color> pointEntry : resultsPoints.entrySet()) {
                Point3DAttribute point = (Point3DAttribute) result.getAttributes().get(pointEntry.getKey());
                if (point == null) {
                    continue;
                }
                drawPoint(g2, point, pointEntry.getValue(), xoff, yoff, zoom);
            }
            // -----------------------------------------------------------
        }
    }

    void renderSearchCfg(Graphics2D g2, float xoff, float yoff, float zoom) throws Exception {
        if (getSearchCfg() == null) {
            return;
        }
        // -----------------------------------------------------------
        // Render configured lines
        // -----------------------------------------------------------
        for (Entry<String, TreeMap<String, Color>> lineEntry : searchLines.entrySet()) {
            Point3DAttribute start = (Point3DAttribute) getSearchCfg().getAttributes().get(lineEntry.getKey());
            if (start == null) {
                continue;
            }
            for (Entry<String, Color> endLineEntry: lineEntry.getValue().entrySet()) {
                Point3DAttribute end = (Point3DAttribute) getSearchCfg().getAttributes().get(endLineEntry.getKey());
                if (end == null) {
                    continue;
                }
                drawLine(g2, start, end, endLineEntry.getValue(), xoff, yoff, zoom);
            }
        }
        // -----------------------------------------------------------

        // -----------------------------------------------------------
        // Render configured points
        // -----------------------------------------------------------
        for (Entry<String, Color> pointEntry : searchPoints.entrySet()) {
            Point3DAttribute point = (Point3DAttribute) getSearchCfg().getAttributes().get(pointEntry.getKey());
            if (point == null) {
                continue;
            }
            drawPoint(g2, point, pointEntry.getValue(), xoff, yoff, zoom);
        }
        // -----------------------------------------------------------
    }

    class MapBounds {
        public MapBounds(float xmin, float ymin, float xmax, float ymax ) {
            min = new MapCoord(xmin, ymin);
            max = new MapCoord(xmax, ymax);
        }
        MapCoord min;
        MapCoord max;
        int pointCount = 0;
        void update(Point3DAttribute ecefpoint) {
            pointCount++;
            MapCoord point = EcefToMap(new EcefVector(0, ecefpoint.getPoint()));

            min.x = Math.min(min.x, point.x);
            min.y = Math.min(min.y, point.y);

            max.x = Math.max(max.x, point.x);
            max.y = Math.max(max.y, point.y);
        }
    }

    MapBounds getpointBounds() throws Exception {
        MapBounds bounds = new MapBounds(mapWidth, mapHeight, 0, 0);

        for (SearchResult result : getResults()) {

            // -----------------------------------------------------------
            for (Entry<String, TreeMap<String, Color>> lineEntry : resultsLines.entrySet()) {
                Point3DAttribute start = (Point3DAttribute) result.getAttributes().get(lineEntry.getKey());
                if (start == null) {
                    continue;
                }

                for (Entry<String, Color> endLineEntry: lineEntry.getValue().entrySet()) {
                    Point3DAttribute end = (Point3DAttribute) result.getAttributes().get(endLineEntry.getKey());
                    if (end == null) {
                        continue;
                    }

                    bounds.update(start);
                    bounds.update(end);
                }
            }
            // -----------------------------------------------------------

            // -----------------------------------------------------------
            for (Entry<String, Color> pointEntry : resultsPoints.entrySet()) {
                Point3DAttribute point = (Point3DAttribute) result.getAttributes().get(pointEntry.getKey());
                if (point == null) {
                    continue;
                }
                bounds.update(point);
            }
            // -----------------------------------------------------------
        }

        if (getSearchCfg() != null) {
            // -----------------------------------------------------------
            for (Entry<String, TreeMap<String, Color>> lineEntry : searchLines.entrySet()) {
                Point3DAttribute start = (Point3DAttribute) getSearchCfg().getAttributes().get(lineEntry.getKey());
                if (start == null) {
                    continue;
                }
                for (Entry<String, Color> endLineEntry: lineEntry.getValue().entrySet()) {
                    Point3DAttribute end = (Point3DAttribute) getSearchCfg().getAttributes().get(endLineEntry.getKey());
                    if (end == null) {
                        continue;
                    }
                    bounds.update(start);
                    bounds.update(end);
                }
            }
            // -----------------------------------------------------------

            // -----------------------------------------------------------
            for (Entry<String, Color> pointEntry : searchPoints.entrySet()) {
                Point3DAttribute point = (Point3DAttribute) getSearchCfg().getAttributes().get(pointEntry.getKey());
                if (point == null) {
                    continue;
                }
                bounds.update(point);
            }
            // -----------------------------------------------------------
        }
        // Need at least 2 points for the bounds to be relevant
        if (bounds.pointCount < 2) {
            return new MapBounds(0, 0, mapWidth, mapHeight);
        }

        return bounds;
    }


    //	private float osToPixX(float osx) {
    //		return (osx+xoffset) / xscale;
    //	}
    //
    //	private float osToPixY(float osy) {
    //		return mapHeight - (osy+yoffset) / yscale;
    //	}

    private MapCoord EcefToMap(Point3DAttribute vector) {
        return EcefToMap(new EcefVector(0, vector.getPoint()));
    }

    private MapCoord EcefToMap(EcefVector vector) {
        OsgbGridCoord os = OsgbConv.convert(vector);
        whirlwind.demo.gui.CoordRemap.Result r = remap.convert((float)os.easting, (float)os.northing);
        return new MapCoord(r.x, r.y);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        try {

            // Calculate the bounding box of all the vectors, in map pixel coordinates
            // Default the viewport to view everything if there are no vectors
            MapBounds bounds = getpointBounds();

            // Calculate the scale factor to map all the vectors into the visible area, excluding the border area
            float xzoom = (bounds.max.x-bounds.min.x) / (getWidth()-2*pixBorder);
            float yzoom = (bounds.max.y-bounds.min.y) / (getHeight()-2*pixBorder);

            float zoom = Math.max(xzoom, yzoom);

            // Add the border padding on
            float border = zoom * pixBorder;
            bounds.min.x -= border;
            bounds.min.y -= border;
            bounds.max.x += border;
            bounds.max.y += border;

            // centre the vectors up and pad spare space left and right, or top and bottom, with map
            if (xzoom < yzoom) {
                float extra = getWidth()*zoom - (bounds.max.x-bounds.min.x);
                extra *= 0.5f;
                bounds.min.x -= extra;
                bounds.max.x += extra;
            } else {
                float extra = getHeight()*zoom - (bounds.max.y-bounds.min.y);
                extra *= 0.5f;
                bounds.min.y -= extra;
                bounds.max.y += extra;
            }

            Graphics2D g2 = (Graphics2D) g;

            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,
                    RenderingHints.VALUE_STROKE_PURE);
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);

            // Draw the map
            g2.drawImage(map, 0, 0,
                    (int)((bounds.max.x-bounds.min.x)/zoom),
                    (int)((bounds.max.y-bounds.min.y)/zoom),
                    (int) bounds.min.x,
                    (int) bounds.min.y,
                    (int) bounds.max.x,
                    (int) bounds.max.y,
                    null);

            // Draw the lines
            renderResults(g2, -bounds.min.x, -bounds.min.y, 1/zoom);
            renderSearchCfg(g2, -bounds.min.x, -bounds.min.y, 1/zoom);


            // Draw the OS grid
            if (bounds.pointCount == 0) {
                for (int x = 0; x < gridxcount; x++) {
                    for (int y = 0; y < gridycount; y++) {
                        for (int w = 0; w < gridsegs; w++) {
                            float step1 = ((float)w)/gridsegs;
                            float step2 = (w+1f)/gridsegs;
                            if (x+1<gridxcount) {
                                whirlwind.demo.gui.CoordRemap.Result a = remap.convert((x+step1)*gridxinc+gridxstart, y*gridyinc+gridystart);
                                whirlwind.demo.gui.CoordRemap.Result b = remap.convert((x+step2)*gridxinc+gridxstart, (y)*gridyinc+gridystart);


                                float x1 = (a.x - bounds.min.x) / zoom;
                                float y1 = (a.y - bounds.min.y) / zoom;
                                float x2 = (b.x - bounds.min.x) / zoom;
                                float y2 = (b.y - bounds.min.y) / zoom;

                                if (y%10 == 0) {
                                    g2.setPaint(gridColour);
                                } else {
                                    g2.setPaint(gridMinorColour);
                                }
                                g2.draw(new Line2D.Float(x1, y1, x2, y2));
                            }
                            if (y+1<gridycount) {

                                whirlwind.demo.gui.CoordRemap.Result a = remap.convert(x*gridxinc+gridxstart, (y+step1)*gridyinc+gridystart);
                                whirlwind.demo.gui.CoordRemap.Result b = remap.convert(x*gridxinc+gridxstart, (y+step2)*gridyinc+gridystart);


                                float x1 = (a.x - bounds.min.x) / zoom;
                                float y1 = (a.y - bounds.min.y) / zoom;
                                float x2 = (b.x - bounds.min.x) / zoom;
                                float y2 = (b.y - bounds.min.y) / zoom;
                                if (x%10 == 0) {
                                    g2.setPaint(gridColour);
                                } else {
                                    g2.setPaint(gridMinorColour);
                                }

                                g2.draw(new Line2D.Float(x1, y1, x2, y2));
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * This method initializes this
     * 
     * @return void
     */
    @Override
    protected void initialize() {
        this.setSize(600, 600);
        this.setLayout(new GridBagLayout());
    }

    @Override
    protected void onReset() {
    }

    @Override
    protected void onSetResult(SearchResult result) {
    }

    @Override
    protected void onSetSearchCfg(QueryConfig searchCfg) {
    }

    @Override
    public void onPageStart() {
        getResults().clear();
    }

    @Override
    protected void onPageComplete() {
        this.repaint();
    }
}