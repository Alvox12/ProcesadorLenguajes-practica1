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
	private static String NL = System.getProperty("line.separator");
	
	
	enum Estado {
		INICIO, REC_POR, REC_DIV, REC_PAP, REC_PCIERR, REC_PUNTOCOMA, REC_IGUAL, REC_EQUIVALENTE,
		REC_MAYOR, REC_MENOR, REC_MAYIGUAL, REC_MENIGUAL, REC_MAS, REC_MENOS, REC_ID, REC_ENT, 
		REC_DEC, REC_EXP, REC_SEPA1, REC_SEPA2, REC_DIF1, REC_DIF2, REC_EOF
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
		while(true) {
			switch(estado) {
				case INICIO: 
					if(hayLetra()) transita(Estado.REC_ID);
					else if(hayDigito()) transita(Estado.REC_ENT);
					else if (haySuma()) transita(Estado.REC_MAS);
					else if (hayResta()) transita(Estado.REC_MENOS);
					else if (hayMul()) transita(Estado.REC_POR);
					else if (hayDiv()) transita(Estado.REC_DIV);
					else if (hayPAp()) transita(Estado.REC_PAP);
					else if (hayPCierre()) transita(Estado.REC_PCIERR);
					else if (hayIgual()) transita(Estado.REC_IGUAL);
					else if (hayMayor()) transita(Estado.REC_MAYOR);
					else if (hayMenor()) transita(Estado.REC_MENOR);
					else if (hayPuntoComa()) transita(Estado.REC_PUNTOCOMA);
					else if (haySep()) transitaIgnorando(Estado.INICIO);
             	 	else if (hayEOF()) transita(Estado.REC_EOF);
             	 	else if (haySeparador()) transita(Estado.REC_SEPA1);
             	 	else if(hayExclamacion()) transita(Estado.REC_DIF1);
              		else error();
					break;
				case REC_ID:
					if (hayLetra() || hayDigito() || hayBarrabaja()) transita(Estado.REC_ID);
		            else return unidadId();     
					break;
				case REC_ENT:
					if (hayDigito()) transita(Estado.REC_ENT);
		            else if (hayPunto()) transita(Estado.REC_DEC);
		            else if(hayExponente()) transita(Estado.REC_EXP);
		            else return unidadEnt();
		            break;
				case REC_DEC: 
		               if (hayDigito()) transita(Estado.REC_DEC);
		               else if(hayExponente()) transita(Estado.REC_EXP);
		               else return unidadReal();
		               break;
				case REC_EXP:
					if(haySuma()) transita(Estado.REC_MAS);
					else if(hayResta()) transita(Estado.REC_MENOS);
					else if(hayDigito()) transita(Estado.REC_ENT);
					break;
				case REC_MAS:
		            if (hayDigito()) transita(Estado.REC_ENT);
		            else return unidadMas();
		            break;
				case REC_MENOS: 
		            if (hayDigito()) transita(Estado.REC_ENT);
		            else return unidadMenos();
		            break;
				case REC_MAYOR: 
					if(hayIgual()) transita(Estado.REC_MAYIGUAL);
					else return unidadMayor();
					break;
				case REC_MENOR: 
					if(hayIgual()) transita(Estado.REC_MENIGUAL);
					else return unidadMenor();
					break;
				case REC_MAYIGUAL: return unidadMayorIgual();
				case REC_MENIGUAL: return unidadMenorIgual();
				case REC_POR: return unidadPor();
		        case REC_DIV: return unidadDiv();              
		        case REC_PAP: return unidadPAp();
		        case REC_PCIERR: return unidadPCierre();
		        case REC_IGUAL: 
		        	if(hayIgual()) transita(Estado.REC_EQUIVALENTE);
		        	else return unidadIgual();
		        	break;
		        case REC_EQUIVALENTE: 
		        	return unidadEquivalente();
		        case REC_DIF1:
		        	if(hayIgual()) transita(Estado.REC_DIF2);
		        	else error(); 
		        	break;
		        case REC_DIF2:
		        	return unidadDiferente();
				case REC_SEPA1:
					if(haySeparador()) transita(Estado.REC_SEPA2);
					else error();
					break;
				case REC_SEPA2:
					return unidadSeparador();
				case REC_PUNTOCOMA:
					return unidadPuntoComa();
				case REC_EOF: return unidadEof();
			}
		}

	}
	
	private void transita(Estado sig) throws IOException {
		lexema.append((char) sigCar);
		sigCar();
		estado = sig;
	}

	private void transitaIgnorando(Estado sig) throws IOException {
		sigCar();
		filaInicio = filaActual;
		columnaInicio = columnaActual;
		estado = sig;
	}

	private void sigCar() throws IOException {
		sigCar = input.read();
		if (sigCar == NL.charAt(0))
			saltaFinDeLinea();
		if (sigCar == '\n') {
			filaActual++;
			columnaActual = 0;
		} else {
			columnaActual++;
		}
	}

	private void saltaFinDeLinea() throws IOException {
		for (int i = 1; i < NL.length(); i++) {
			sigCar = input.read();
			if (sigCar != NL.charAt(i))
				error();
		}
		sigCar = '\n';
	}

	private boolean hayLetra() {
		return sigCar >= 'a' && sigCar <= 'z' ||
		sigCar >= 'A' && sigCar <= 'z';
	}
	
	private boolean haySeparador() {
		return sigCar == '&'; 
	}
	
	private boolean hayExclamacion() {
		return sigCar == '!';
	}
	
	private boolean hayDigito() {
		return sigCar >= '0' && sigCar <= '9';
	}
	
	private boolean haySuma() {
		return sigCar == '+';
	}
	
	private boolean hayResta() {
		return sigCar == '-';
	}
	
	private boolean hayMul() {return sigCar == '*';}
	
	private boolean hayDiv() {return sigCar == '/';}
	
	private boolean hayPAp() {return sigCar == '(';}
	
	private boolean hayPCierre() {return sigCar == ')';}
	
	private boolean hayIgual() {return sigCar == '=';}
	
	private boolean hayMayor() {return sigCar == '>';}
	
	private boolean hayMenor() {return sigCar == '<';}
	
	private boolean hayPunto() {
		return sigCar == '.';
	}

	private boolean hayPuntoComa() {
		return sigCar == ';';
	}
	
	private boolean hayBarrabaja() {
		return sigCar == '_';
	}

	private boolean haySep() {
		return sigCar == ' ' || sigCar == '\t' || sigCar == '\n';
	}
	
	private boolean hayExponente() {
		return sigCar == 'e'||sigCar == 'E';
	}
	
	private boolean hayEOF() {return sigCar == -1;}
	
	private UnidadLexica unidadId() {
	     switch(lexema.toString()) {
	         case "num":  
	            return new UnidadLexicaUnivaluada(filaInicio,columnaInicio,ClaseLexica.NUM);
	         case "bool":    
	            return new UnidadLexicaUnivaluada(filaInicio,columnaInicio,ClaseLexica.BOOL);
	         case "or": 
	        	return new UnidadLexicaUnivaluada(filaInicio,columnaInicio,ClaseLexica.OR);
	         case "and": 
	        	return new UnidadLexicaUnivaluada(filaInicio,columnaInicio,ClaseLexica.AND);
	         case "not": 
	        	return new UnidadLexicaUnivaluada(filaInicio,columnaInicio,ClaseLexica.NOT);
	         case "true":
	        	 return new UnidadLexicaUnivaluada(filaInicio,columnaInicio,ClaseLexica.TRUE);
	         case "false":
	        	 return new UnidadLexicaUnivaluada(filaInicio,columnaInicio,ClaseLexica.FALSE);
	         default:    
	            return new UnidadLexicaMultivaluada(filaInicio,columnaInicio,ClaseLexica.IDEN,lexema.toString());     
	      }
	   } 
	
	private UnidadLexica unidadEnt() {
	     return new UnidadLexicaMultivaluada(filaInicio,columnaInicio,ClaseLexica.ENT,lexema.toString());     
	}
	
	private UnidadLexica unidadReal() {
	     return new UnidadLexicaMultivaluada(filaInicio,columnaInicio,ClaseLexica.REAL,lexema.toString());     
	}
	
	private UnidadLexica unidadSeparador() {
		return new UnidadLexicaUnivaluada(filaInicio,columnaInicio,ClaseLexica.SEPARADOR);
	}
	
	private UnidadLexica unidadDiferente() {
		return new UnidadLexicaUnivaluada(filaInicio,columnaInicio,ClaseLexica.DIFERENTE);
	}
	
	private UnidadLexica unidadMas() {
	     return new UnidadLexicaUnivaluada(filaInicio,columnaInicio,ClaseLexica.MAS);     
	}    
	private UnidadLexica unidadMenos() {
	     return new UnidadLexicaUnivaluada(filaInicio,columnaInicio,ClaseLexica.MENOS);     
	}    
	private UnidadLexica unidadPor() {
	     return new UnidadLexicaUnivaluada(filaInicio,columnaInicio,ClaseLexica.POR);     
	}    
	private UnidadLexica unidadDiv() {
	     return new UnidadLexicaUnivaluada(filaInicio,columnaInicio,ClaseLexica.DIV);     
	}    
	private UnidadLexica unidadPAp() {
	     return new UnidadLexicaUnivaluada(filaInicio,columnaInicio,ClaseLexica.PAP);     
	}    
	private UnidadLexica unidadPCierre() {
	     return new UnidadLexicaUnivaluada(filaInicio,columnaInicio,ClaseLexica.PCIERRE);     
	}
	private UnidadLexica unidadMayor() {
	     return new UnidadLexicaUnivaluada(filaInicio,columnaInicio,ClaseLexica.MAYOR);     
	}
	private UnidadLexica unidadMenor() {
	     return new UnidadLexicaUnivaluada(filaInicio,columnaInicio,ClaseLexica.MENOR);     
	}
	private UnidadLexica unidadMayorIgual() {
	     return new UnidadLexicaUnivaluada(filaInicio,columnaInicio,ClaseLexica.MAYIGUAL);     
	} 
	private UnidadLexica unidadMenorIgual() {
	     return new UnidadLexicaUnivaluada(filaInicio,columnaInicio,ClaseLexica.MENIGUAL);     
	} 
	private UnidadLexica unidadIgual() {
	     return new UnidadLexicaUnivaluada(filaInicio,columnaInicio,ClaseLexica.IGUAL);     
	}
	private UnidadLexica unidadEquivalente() {
	     return new UnidadLexicaUnivaluada(filaInicio,columnaInicio,ClaseLexica.EQUIVALENTE);     
	}
	private UnidadLexica unidadPuntoComa() {
	     return new UnidadLexicaUnivaluada(filaInicio,columnaInicio,ClaseLexica.PUNTOCOMA);     
	}
	private UnidadLexica unidadEof() {
	     return new UnidadLexicaUnivaluada(filaInicio,columnaInicio,ClaseLexica.EOF);     
	}

	private void error() {
		System.err.println("(" + filaActual + ',' + columnaActual + "):Caracter inexperado");
		System.exit(1);
	}


}




















