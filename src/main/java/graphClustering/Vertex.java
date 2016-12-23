package graphClustering;

public class Vertex {
    private static int counter = 0;
	  final private int id;
	  final private String name;
	  
	  
	  public Vertex(String name) {
	    this.id = counter++;
	    this.name = name;
	  }


	  public String getName() {
	    return name;
	  }
	  public int getId() {
		return id;
	}

	  
	  @Override
	  public boolean equals(Object obj) {
		  Vertex other = (Vertex) obj;
	    if (this.getName().equals(other.getName()) || this.getId()==other.getId()) 
	      return true;
	    else
	    	return false;
	  }

	  @Override
	  public String toString() {
	    return name;
	  }
	  
	} 