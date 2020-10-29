package pe.edu.upc.entity;

public enum Moneda{
	sol("Sol"), dolar("Dolar"), euro("Euro");
	
	public final String label;
	
	private Moneda(String label) {
		this.label = label;
	}
	
	@Override
	public String toString() {
		return this.label;
	}
}