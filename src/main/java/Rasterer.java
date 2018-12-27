import java.util.HashMap;
import java.util.Map;

/**
 * This class provides all code necessary to take a query box and produce
 * a query result. The getMapRaster method must return a Map containing all
 * seven of the required fields, otherwise the front end code will probably
 * not draw the output correctly.
 */
public class Rasterer {

    private final double ROOT_ULLAT = 37.892195547244356, ROOT_ULLON = -122.2998046875,
            ROOT_LRLAT = 37.82280243352756, ROOT_LRLON = -122.2119140625;
    private final double lonDpp1 = 0.000171661376953125;
    private double rasterUlLon, rasterUlLat, rasterLrLon,
            rasterLrLat, raster_startX, raster_startY,
            raster_endX, raster_endY;
    private boolean querySuccess = true;


    public Rasterer() {
        // YOUR CODE HERE
    }

    /**
     * Takes a user query and finds the grid of images that best matches the query. These
     * images will be combined into one big image (rastered) by the front end. <br>
     * <p>
     * The grid of images must obey the following properties, where image in the
     * grid is referred to as a "tile".
     * <ul>
     * <li>The tiles collected must cover the most longitudinal distance per pixel
     * (LonDPP) possible, while still covering less than or equal to the amount of
     * longitudinal distance per pixel in the query box for the user viewport size. </li>
     * <li>Contains all tiles that intersect the query bounding box that fulfill the
     * above condition.</li>
     * <li>The tiles must be arranged in-order to reconstruct the full image.</li>
     * </ul>
     *
     * @param params Map of the HTTP GET request's query parameters - the query box and
     *               the user viewport width and height.
     * @return A map of results for the front end as specified: <br>
     * "render_grid"   : String[][], the files to display. <br>
     * "raster_ul_lon" : Number, the bounding upper left longitude of the rastered image. <br>
     * "raster_ul_lat" : Number, the bounding upper left latitude of the rastered image. <br>
     * "raster_lr_lon" : Number, the bounding lower right longitude of the rastered image. <br>
     * "raster_lr_lat" : Number, the bounding lower right latitude of the rastered image. <br>
     * "depth"         : Number, the depth of the nodes of the rastered image <br>
     * "query_success" : Boolean, whether the query was able to successfully complete; don't
     * forget to set this to true on success! <br>
     */
    public Map<String, Object> getMapRaster(Map<String, Double> params) {
        //System.out.println(params);
        Map<String, Object> results = new HashMap<>();
        //System.out.println("Since you haven't implemented getMapRaster, nothing is displayed in "
        //+ "your browser.");
        double lonDppR = (params.get("lrlon") - params.get("ullon")) / params.get("w");
        int depth = findDepth(lonDppR);
        String[][] renderGrid = createRenderGrid(depth, params.get("ullon"),
                params.get("lrlon"), params.get("ullat"), params.get("lrlat"));
        results.put("render_grid", renderGrid);
        results.put("depth", depth);
        results.put("query_success", querySuccess);
        setRasterLonLat(depth);
        results.put("raster_ul_lon", rasterUlLon);
        results.put("raster_ul_lat", rasterUlLat);
        results.put("raster_lr_lon", rasterLrLon);
        results.put("raster_lr_lat", rasterLrLat);

        return results;
    }

    private void setRasterLonLat(int depth) {
        double imageWidth = Math.abs(ROOT_LRLON - ROOT_ULLON) / Math.pow(2, depth);
        double imageHeight = Math.abs(ROOT_ULLAT - ROOT_LRLAT) / Math.pow(2, depth);

        rasterLrLon = ROOT_ULLON + imageWidth * (raster_endX + 1);
        rasterUlLon = ROOT_ULLON + imageWidth * raster_startX;
        rasterUlLat = ROOT_ULLAT - imageHeight * raster_startY;
        rasterLrLat = ROOT_ULLAT - imageHeight * (raster_endY + 1);
    }

    public int findDepth(double lonDppR) {
        double lonDpp = lonDpp1 * 2;
        int i = 0;

        while (lonDpp > lonDppR && i < 7) {
            lonDpp /= 2;
            i++;
        }

        return i;
    }

    public String[][] createRenderGrid(int depth, double lLon, double rLon,
                                       double uLat, double bLat) {
        String[][] renderGrid;
        int startX, startY, width, height;
        double imageWidth = Math.abs(ROOT_LRLON - ROOT_ULLON) / Math.pow(2, depth);
        double imageHeight = Math.abs(ROOT_ULLAT - ROOT_LRLAT) / Math.pow(2, depth);

        if (rLon <= ROOT_ULLON || lLon >= ROOT_LRLON || uLat <= ROOT_LRLAT || bLat >= ROOT_ULLAT) {
            querySuccess = false;
            return null;
        } else {
            startX = getStartX(depth, lLon);
            raster_startX = startX;
            startY = getStartY(depth, uLat);
            raster_startY = startY;
            width = getWidth(imageWidth, startX, rLon);
            height = getHeight(imageHeight, startY, bLat);
            renderGrid = new String[height][width];

            for (int j = 0; j < height; j++) {
                for (int i = 0; i < width; i++) {
                    renderGrid[j][i] = "d" + depth + "_x" + (startX + i)
                            + "_y" + (startY + j) + ".png";
                    if (j == height - 1 && i == width - 1) {
                        raster_endX = i + startX;
                        raster_endY = j + startY;
                    }
                }
            }
        }

        return renderGrid;
    }

    private int getHeight(double imageHeight, int startY, double bLat) {
        int i = 0;
        double startYLat = ROOT_ULLAT - imageHeight * startY;
        while (startYLat > bLat && startYLat > ROOT_LRLAT) {
            i++;
            startYLat -= imageHeight;
        }
        return i;
    }

    private int getWidth(double imageWidth, int startX, double rLon) {
        int i = 0;
        double startXLon = ROOT_ULLON + imageWidth * startX;
        while (startXLon < rLon && startXLon < ROOT_LRLON) {
            i++;
            startXLon += imageWidth;
        }
        return i;
    }

    private int getStartX(int depth, double lLon) {
        if (lLon < ROOT_ULLON) {
            lLon = ROOT_ULLON;
        }

        double imageWidth = Math.abs(ROOT_LRLON - ROOT_ULLON) / Math.pow(2, (depth));
        double closestLLon = imageWidth + ROOT_ULLON;
        int startX = 1;

        while (closestLLon < lLon) {
            closestLLon += imageWidth;
            startX++;
        }

        startX--;
        return startX;
    }

    private int getStartY(int depth, double uLat) {
        if (uLat > ROOT_ULLAT) {
            uLat = ROOT_ULLAT;
        }

        double imageHeight = Math.abs(ROOT_ULLAT - ROOT_LRLAT) / Math.pow(2, (depth));
        double closestLLon = ROOT_ULLAT - imageHeight;
        int startY = 1;

        while (closestLLon > uLat) {
            closestLLon -= imageHeight;
            startY++;
        }

        startY--;
        return startY;
    }
}
