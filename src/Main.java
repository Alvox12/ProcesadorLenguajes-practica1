import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

public class Main {

	public static void main(String[] args) throws IOException {
		Reader input = new InputStreamReader(new FileInputStream("input4.txt"));
		AnalizadorLexico al = new AnalizadorLexico(input);
		UnidadLexica unidad;
		do {
			unidad = al.sigToken();
			System.out.println(unidad);
		} while (unidad.clase() != ClaseLexica.EOF);
	}

}
