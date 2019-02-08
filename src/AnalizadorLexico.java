import java.io.IOException;
import java.io.Reader;

public class AnalizadorLexico {

	private Reader input; // flujo de entrada
	private StringBuffer lexema; // cadena para almacenar el lexema de la
	// u. l�xica
	private int sigCar; // siguiente car�cter a procesar
	private int filaInicio; // fila inicio de la u. l�xica
	private int columnaInicio; // col. inicio de la u. l�xica
	private int filaActual;
	private int columnaActual;
	private Estado estado; // estado del aut�mata
	
	
	enum Estado {
		INICIO, REC_POR, REC_DIV, REC_PAP, REC_PCIERR, REC_COMA, REC_IGUAL,
		REC_MAS, REC_MENOS, REC_ID, REC_ENT, REC_0, REC_IDEC, REC_DEC,
		REC_COM,REC_EOF
	}
	
	public AnalizadorLexico(Reader input) throws IOException {
		this.input = input;
		lexema = new StringBuffer();
		sigCar = input.read();
		filaActual=1;
		columnaActual=1;
	}

	public UnidadLexica sigToken() throws IOException {
		
		// Fase de inicializaci�n
		estado = Estado.INICIO;
		filaInicio = filaActual;
		columnaInicio = columnaActual;
		lexema.delete(0,lexema.length());
		
		return null;
	}
	
	private boolean hayLetra() {
		return sigCar >= 'a' && sigCar <= 'z' ||
		sigCar >= 'A' && sigCar <= 'z';
	}
	
	private boolean hayCero() {
		return sigCar == 0;
	}
	
	private boolean hayDigitoPos() {
		return sigCar >= 1 && sigCar <= 9;
	}
	
	private boolean hayDigito() {
		return hayDigitoPos()||hayCero();
	}
	
	private boolean hayPunto() {
		return sigCar == '.';
	}
	
	private boolean hayAlmohadilla() {
		return sigCar == '#';
	}

}




















