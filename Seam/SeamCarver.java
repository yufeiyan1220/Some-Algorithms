import edu.princeton.cs.algs4.Picture;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.AcyclicSP;
import edu.princeton.cs.algs4.DirectedEdge;
import edu.princeton.cs.algs4.EdgeWeightedDigraph;

public class SeamCarver {
	private Picture picture;
	EdgeWeightedDigraph G;
	AcyclicSP sp;
	public SeamCarver(Picture picture) {
		if (picture == null) {
			throw new IllegalArgumentException("argument to SeamCarver() is null\n"); 
		}
		this.picture = new Picture(picture);
	}                // create a seam carver object based on the given picture
	public Picture picture() {
	   return picture;
	}                          // current picture
	public int width() {
	   return picture.width();
	}                        // width of current picture
	public int height() {
	   return picture.height();
	}                          // height of current picture
	public double energy(int x, int y) {
	   if (x == 0 || y == 0 || x == picture.width()-1 || y == picture.height()-1) return 1000;
	   else {
		   int Rx = picture.get(x + 1, y).getRed() - picture.get(x - 1, y).getRed();
		   int Ry = picture.get(x, y + 1).getRed() - picture.get(x, y - 1).getRed();
		   int Bx = picture.get(x + 1, y).getBlue() - picture.get(x - 1, y).getBlue();
		   int By = picture.get(x, y + 1).getBlue() - picture.get(x, y - 1).getBlue();
		   int Gx = picture.get(x + 1, y).getGreen() - picture.get(x - 1, y).getGreen();
		   int Gy = picture.get(x, y + 1).getGreen() - picture.get(x, y - 1).getGreen();
		   return Math.sqrt(Rx * Rx + Ry * Ry + Bx * Bx + By * By + Gx * Gx + Gy * Gy);
	   }
	}              // energy of pixel at column x and row y
	public int[] findHorizontalSeam() {
		this.G = new EdgeWeightedDigraph((picture.width() - 2) * picture.height() + 2);
		 
		transpose();
		int[] index_h = findVerticalSeam();
		transpose();
		return index_h;
	}              // sequence of indices for horizontal seam
	public int[] findVerticalSeam() {
		this.G = new EdgeWeightedDigraph(picture.width() * (picture.height()-2) + 2);
		 
		for (int j = 0 ; j < width(); j++) {
			G.addEdge(new DirectedEdge(width() * (height() - 2), j, energy(j, 1)));
			G.addEdge(new DirectedEdge(width() * (height() - 3) + j, width() * (height() - 2) + 1, energy(j, picture.height() - 2)));
		}
		for (int i = 0 ; i < height() - 2; i++)
			for (int j = 0; j < width(); j++) {
				int v = i * width() + j;
				int w = (i + 1) * width() + j;
				if (i < height() - 3) {
					G.addEdge(new DirectedEdge(v, w, energy(j, i + 1)));
					if (j != 0)
						G.addEdge(new DirectedEdge(v, w - 1, energy(j - 1, i + 1)));
					if (j != width() - 1)
						G.addEdge(new DirectedEdge(v, w + 1, energy(j + 1, i + 1)));
				}
			}
		sp = new AcyclicSP(G, width() * (height() - 2));
		Stack<DirectedEdge> path = (Stack<DirectedEdge>)sp.pathTo(width() * (height() - 2) + 1);
		int[] index_v = new int[height()];
		for (int i = 1; i < height() - 1; i++) {
			if (i == 1) {
				index_v[1] = path.pop().to();
				index_v[0] = index_v[1];
			}
			else index_v[i] = path.pop().to() - width() * (i - 1);
		}
		index_v[height() - 1] = index_v[height() - 2];
		return index_v;
	}       // sequence of indices for vertical seam
	public void removeVerticalSeam(int[] seam) {
		if (seam == null) {
            throw new IllegalArgumentException("the argument to removeVerticalSeam() is null\n"); 
        }
        if (seam.length != height()) {
            throw new IllegalArgumentException("the length of seam not equal height\n");
        }
        validateSeam(seam);
        if (width() <= 1) {
            throw new IllegalArgumentException("the width of the picture is less than or equal to 1\n");
        }

        Picture tmpPicture = new Picture(width() - 1, height());
        for (int row = 0; row < height(); row++) {
            for (int col = 0; col < width() - 1; col++) {
                validateColumnIndex(seam[row]);
                if (col < seam[row]) {
                    tmpPicture.setRGB(col, row, picture.getRGB(col, row));
                } else {
                    tmpPicture.setRGB(col, row, picture.getRGB(col + 1, row));
                }
            }
        }
        picture = tmpPicture;

	}  // remove horizontal seam from current picture
	public void removeHorizontalSeam(int[] seam) {
		if (seam == null) {
            throw new IllegalArgumentException("the argument to removeHorizontalSeam() is null\n"); 
        }
        if (seam.length != width()) {
            throw new IllegalArgumentException("the length of seam not equal width\n");
        }
        validateSeam(seam);
        if (height() <= 1) {
            throw new IllegalArgumentException("the height of the picture is less than or equal to 1\n");
        }

        transpose();
        removeVerticalSeam(seam);
        transpose();
	}    // remove vertical seam from current picture
	private void transpose() {
        Picture tmpPicture = new Picture(height(), width());
        for (int row = 0; row < width(); row++) {
            for (int col = 0; col < height(); col++) {
                tmpPicture.setRGB(col, row, picture.getRGB(row, col));
            }
        } 
        picture = tmpPicture;
     }
	
    private void validateColumnIndex(int col) {
        if (col < 0 || col > width() -1) {
            throw new IllegalArgumentException("colmun index is outside its prescribed range\n"); 
        }
    }

    // make sure row index is bewteen 0 and height - 1
    private void validateRowIndex(int row) {
        if (row < 0 || row > height() -1) {
            throw new IllegalArgumentException("row index is outside its prescribed range\n"); 
        }
    }
    private void relax() {
    	
    }     // make sure two adjacent entries differ within 1
    private void validateSeam(int[] seam) {
        for (int i = 0; i < seam.length - 1; i++) {
            if (Math.abs(seam[i] - seam[i + 1]) > 1) {
                throw new IllegalArgumentException("two adjacent entries differ by more than 1 in seam\n"); 
            }
        }
    }
}

