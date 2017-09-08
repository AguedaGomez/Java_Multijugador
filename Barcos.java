import java.net.*;
import java.io.*;
import java.util.Scanner;
import java.util.Random;

public class Barcos {
	
	public static void main (String[] args) {
		
		int puerto1 = Integer.parseInt(args[0]);
		int puerto2 = Integer.parseInt(args[1]);
		String IP2 = args[2];//"127.0.0.1";
		
		boolean conectado1 = false;
		boolean conectado2 = false;
		Socket socketCliente = null;
		ServerSocket socketServidor = null;
		Socket conexion = null;
		
		PrintWriter envia = null;
		BufferedReader recibe = null;
		

		Scanner jugada = new Scanner (System.in);
		
		String columnas = "   A   B   C   D   E  \n";
		String separacion = " |---|---|---|---|---|\n";
		String fila_1 = "1|   |   |   |   |   |\n";
		String fila_2 = "2|   |   |   |   |   |\n";
		String fila_3 = "3|   |   |   |   |   |\n";
		String fila_4 = "4|   |   |   |   |   |\n";
		char[] f1 = fila_1.toCharArray();
		char[] f2 = fila_2.toCharArray();
		char[] f3 = fila_3.toCharArray();
		char[] f4 = fila_4.toCharArray();
		
		char[] fBarcos1 = fila_1.toCharArray();
		char[] fBarcos2 = fila_2.toCharArray();
		char[] fBarcos3 = fila_3.toCharArray();
		char[] fBarcos4 = fila_4.toCharArray();

		
		
				
		String fila = null;
		String columna = null;
		boolean juegoTerminado = false;
		boolean grandeHundido = false;
		boolean pequeno1Hundido = false;
		boolean pequeno2Hundido = false;
		boolean correctInput = false;
		int barcosHundidos = 0;
		int barcosEnemigosHundidos = 0;
		String ataqueRecibido = null;
		String[] posicionBarcos = new String[7];
		String[][] configuraciones = {{"2A", "2B", "2C", "3E", "4E", "4B", "4C"}, {"2D", "3D", "4D", "1A", "1B", "3B", "4B"}, {"1C", "1D", "1E", "3A", "3B", "3D", "4D"}, {"1E", "2E", "3E", "1A", "2A", "3A", "3B"}, {"4C", "4D", "4E", "2A", "2B", "2E", "3E"}, {"1B", "1C", "1D", "2C", "3C", "4D", "4E"} };
		Random aleatorio = new Random();
		
		try {
			
			socketCliente = new Socket(IP2, puerto2);
			socketCliente.setSoTimeout(15000);
			System.out.println ("Exito en la conexion. Servidor: " + IP2 + " Puerto: " + puerto2 + " PC1 conectado\n");
			conectado2 = true;
			
		}
		catch (IOException ex) {
			
			System.err.println (ex + " No hay conexion\n");

		}
		
		try {
			socketServidor = new ServerSocket(puerto1);
			System.out.println ("Esperando a un cliente");
			conexion = socketServidor.accept();
			System.out.println ("Se ha conectado un cliente");
			
			if(!conectado1 && !conectado2) {
				socketCliente = new Socket(IP2, puerto2); // conexion al otro host
				socketCliente.setSoTimeout(15000);
				System.out.println ("Exito en la conexion. Servidor: " + IP2 + " Puerto: " + puerto2 + " PC2 conectado\n");
				conectado1 = true;
			}
			
			envia = new PrintWriter(socketCliente.getOutputStream(), true);
			recibe = new BufferedReader(new InputStreamReader(conexion.getInputStream()));

		}
		catch (IOException e) {
			
			System.err.println (e + " No es posible realizar la conexion");
		}
		
		
		if (conectado1) {
			int indiceConfig = aleatorio.nextInt(6);
			posicionBarcos = configuraciones[indiceConfig];
			System.out.println("LA POSICION DE TUS BARCOS ES:");
			
			dibujarTableroInicial(posicionBarcos, fBarcos1, fBarcos2, fBarcos3, fBarcos4, columnas, separacion);
			
			while(!juegoTerminado){
				
				//JUGADA DE ATAQUE
				
				System.out.println("TU TURNO\n");
				
				do{
					System.out.println("Elige una fila 1/2/3/4: ");
					fila = jugada.nextLine();
					if( fila.equals("1") || fila.equals("2") || fila.equals("3") || fila.equals("4")  ){  
					correctInput = true; 
					}else{
						correctInput = false;
					}
				}while(!correctInput );
				
				do{
					System.out.println("Elige una columna A/B/C/D/E: ");
					columna = jugada.nextLine();
					if( columna.equals("A") || columna.equals("B") || columna.equals("C") || columna.equals("D") || columna.equals("E")  )
					{ 
						correctInput = true; 
					}else{
						correctInput = false;
					}
					}while(!correctInput );
					
				String ataque = fila + columna;
				envia.println(ataque);
				
				//RESULTADO DE ATAQUE
				try{
					String mov = recibe.readLine();
					System.out.println("Resultado del ataque: " + mov);
					dibujarTablero(mov, f1, f2, f3, f4, columnas, separacion);
					barcosEnemigosHundidos = Integer.parseInt( recibe.readLine() );
					
				}
				catch(IOException e){
					System.err.println (e + " No leo nah!");
				}
				
				//AQUI EL JUEGO PUEDE TERMINAR TAMBIEN
			  //COMPROBACION DE TERMINACION
				if(barcosHundidos == 3 || barcosEnemigosHundidos == 3){
					juegoTerminado = true;
					break; // salir del juego
					}
				
				//ESPERANDO JUGADA DEL ADVERSARIO
				try{
					
					System.out.println("Esperando jugada del adversario\n");
					ataqueRecibido = recibe.readLine();
					System.out.println("Ataque recibido en la posicion " + ataqueRecibido);
				}
				catch(IOException e){
					System.err.println (e + " No leo nah!");
				}
				
								//EVALUANDO JUGADA DEL ADVESARIO Y COMUNICANDO EL RESULTADO
				
				boolean tocado = false;
				
				for (int i = 0; i < posicionBarcos.length ; i++) {
					if (posicionBarcos[i].equals(ataqueRecibido)) { // si el ataque tiene exito

						tocado = true; 
						posicionBarcos[i] = "0"; // eliminamos la posicion guardada en el array
						
						if(posicionBarcos[0].equals("0") && posicionBarcos[1].equals("0") && posicionBarcos[2].equals("0") && !grandeHundido ){ // revisamos si se ha hundido el grande
							System.out.println("Tocado y hundido el barco grande en " + ataqueRecibido); // tocado y hundido
							envia.println("Tocado y hundido el barco grande en " + ataqueRecibido);
							grandeHundido = true;
							barcosHundidos++;
						}else if( posicionBarcos[3].equals("0") && posicionBarcos[4].equals("0") && !pequeno1Hundido){ // revisamos si se ha hundido el pequeño1
							System.out.println("Tocado y hundido el barco pequeno 1 en " + ataqueRecibido); // tocado y hundido
							envia.println("Tocado y hundido el barco pequeno 1 en " + ataqueRecibido);
							pequeno1Hundido = true;
							barcosHundidos++;
						}else if( posicionBarcos[5].equals("0") && posicionBarcos[6].equals("0") && !pequeno2Hundido ){ // revisamos si se ha hundido el pequeño2
							System.out.println("Tocado y hundido el barco pequeno 2 en " + ataqueRecibido); // tocado y hundido
							envia.println("Tocado y hundido el barco pequeno 2 en " + ataqueRecibido);
							pequeno2Hundido = true;
							barcosHundidos++;
						}else{
						System.out.println("Tocado en " + ataqueRecibido); // Sólo tocado
						envia.println("Tocado en " + ataqueRecibido);
						}
						break;
					}
				}
								
				if (!tocado) {
					System.out.println("Agua en " + ataqueRecibido);
					envia.println("Agua en " + ataqueRecibido);
				}
					
				envia.println(""+barcosHundidos); // le enviamos al contrario la cantidad de barcos que me ha hundido.

					
				//COMPROBACION DE TERMINACION ANTES DE ENVIAR ATAQUE
				if(barcosHundidos == 3 || barcosEnemigosHundidos == 3){
					juegoTerminado = true;
					}
				
			}//fin del while(conectado1)
			
		}//fin del if(conectado1)
			
		else if (conectado2){
			int indiceConfig = aleatorio.nextInt(6);
			posicionBarcos = configuraciones[indiceConfig];
			System.out.println("LA POSICION DE TUS BARCOS ES:");
			dibujarTableroInicial(posicionBarcos, fBarcos1, fBarcos2, fBarcos3, fBarcos4, columnas, separacion);
			
			while(!juegoTerminado){
				
				
				//ESPERANDO JUGADA DEL ADVERSARIO
				
				try{
					System.out.println("Esperando jugada del adversario\n");
					ataqueRecibido = recibe.readLine();
					System.out.println("Ataque recibido en la posicion " + ataqueRecibido);
				}
				catch(IOException e){
					System.err.println (e + " No leo nah!");
				}
				
				
				//EVALUANDO JUGADA DEL ADVESARIO Y COMUNICANDO EL RESULTADO
				
				boolean tocado = false;
				
				for (int i = 0; i < posicionBarcos.length ; i++) {
					if (posicionBarcos[i].equals(ataqueRecibido)) { // si el ataque tiene exito

						tocado = true; 
						posicionBarcos[i] = "0"; // eliminamos la posicion guardada en el array
						
						if(posicionBarcos[0].equals("0") && posicionBarcos[1].equals("0") && posicionBarcos[2].equals("0") && !grandeHundido ){ // revisamos si se ha hundido el grande
							System.out.println("Tocado y hundido el barco grande en " + ataqueRecibido); // tocado y hundido
							envia.println("Tocado y hundido el barco grande en " + ataqueRecibido);
							grandeHundido = true;
							barcosHundidos++;
						}else if( posicionBarcos[3].equals("0") && posicionBarcos[4].equals("0") && !pequeno1Hundido){ // revisamos si se ha hundido el pequeño1
							System.out.println("Tocado y hundido el barco pequeno 1 en " + ataqueRecibido); // tocado y hundido
							envia.println("Tocado y hundido el barco pequeno 1 en " + ataqueRecibido);
							pequeno1Hundido = true;
							barcosHundidos++;
						}else if( posicionBarcos[5].equals("0") && posicionBarcos[6].equals("0") && !pequeno2Hundido ){ // revisamos si se ha hundido el pequeño2
							System.out.println("Tocado y hundido barco pequeno 2 en " + ataqueRecibido); // tocado y hundido
							envia.println("Tocado y hundido barco pequeno 2 en " + ataqueRecibido );
							pequeno2Hundido = true;
							barcosHundidos++;
						}else{
						System.out.println("Tocado en " + ataqueRecibido); // Sólo tocado
						envia.println("Tocado en " + ataqueRecibido);
						}						
						break;
					}
				}				
				
				if (!tocado) {
					System.out.println("Agua en " + ataqueRecibido);
					envia.println("Agua en " + ataqueRecibido);
				}
				
				envia.println(""+barcosHundidos); // le enviamos al contrario la cantidad de barcos que me ha hundido.
					
				
				//COMPROBACION DE TERMINACION ANTES DE ENVIAR ATAQUE
				if(barcosHundidos == 3 || barcosEnemigosHundidos == 3){
					juegoTerminado = true;
					break;
				}
				
					//JUGADA DE ATAQUE
				System.out.println("TU TURNO");
				
				do{
					System.out.println("Elige una fila 1/2/3/4: ");
					fila = jugada.nextLine();
					if( fila.equals("1") || fila.equals("2") || fila.equals("3") || fila.equals("4")  ){ 
					correctInput = true; 
					}else{
						correctInput = false;
					}
				}while(!correctInput );
				
				do{
					System.out.println("Elige una columna A/B/C/D/E: ");
					columna = jugada.nextLine();
					if( columna.equals("A") || columna.equals("B") || columna.equals("C") || columna.equals("D") || columna.equals("E")  )
					{ 
					correctInput = true; 
					}else{
						correctInput = false;
					}
					}while(!correctInput );
				
				String ataque = fila + columna;
				envia.println(ataque);
				
				//RESULTADO DE ATAQUE
				try{
					String mov = recibe.readLine();
					System.out.println("Resultado del ataque: " + mov);
					dibujarTablero(mov, f1, f2, f3, f4, columnas, separacion);
					barcosEnemigosHundidos = Integer.parseInt( recibe.readLine() );
					
				}
				catch(IOException e){
					System.err.println (e + " No leo nah!");
				}
			
			//COMPROBACION DE TERMINACION
				if(barcosHundidos == 3 || barcosEnemigosHundidos == 3){
					juegoTerminado = true;
					break;
				}
			
			
			}//fin del while(conectado2)
		
		} //fin del if(conectado2)
			
		System.out.println("");
		System.out.println("");
		System.out.println("----------------------GAME OVER----------------------------");
		System.out.println("Resultado:");
		System.out.println("Barcos del jugador hundidos = " + barcosHundidos);
		System.out.println("Barcos del enemigo hundidos = " + barcosEnemigosHundidos);
		System.out.println("----------------------GAME OVER----------------------------");
		System.out.println("");
		System.out.println("");	
		
		try {
			socketCliente.close();
			conexion.close();
		}
		catch(IOException error) {
			System.err.println (error + "no se ha podido cerrar conexion");
		}
		
		
				
	}//fin del main
	
	static void dibujarTablero(String jugada, char[] fila1, char[] fila2, char[] fila3, char[] fila4, String col, String sep) {
		char[] j = jugada.toCharArray();

		char fila = j[(j.length)-2];
		char r = ' ';
		int columna = 0;
		char letra = j[(j.length)-1];
		
		if (j[0] == 'A')
			conversionPosiciones(columna, fila, letra, 'A', fila1, fila2, fila3, fila4, col, sep);
		else
			conversionPosiciones(columna, fila, letra, 'X', fila1, fila2, fila3, fila4, col, sep);
		

			System.out.println(col+ sep +  new String (fila1) + sep +  new String(fila2) + sep + new String(fila3) + sep + new String(fila4) + sep);
		
		}
	static void conversionPosiciones (int columna, char fila, char letra, char resultado, char[] fila1, char[] fila2, char[] fila3, char[] fila4, String col, String sep) {
			switch (letra) {
			case 'A':
				columna = 3;
				break;
			case 'B':
				columna = 7;
				break;
			case 'C':
				columna = 11;
				break;
			case 'D':
				columna = 15;
				break;
			case 'E':
				columna = 19;
				break;
		}
		
		switch (fila) {
			case '1':
				fila1[columna]= resultado;
				break;
			case '2':
				fila2[columna]= resultado;
				break;
			case '3':
				fila3[columna]= resultado;
				break;
			case '4':
				fila4[columna]= resultado;
				break;
		}
	}
	static void dibujarTableroInicial (String[] posiciones, char[] fila1, char[] fila2, char[] fila3, char[] fila4, String col, String sep) {
		char[] posicion = new char[2];
		int columna = 0;
		for (int i=0; i<posiciones.length; i++) {
			posicion = posiciones[i].toCharArray();
			conversionPosiciones(columna, posicion[0], posicion[1], 'X', fila1, fila2, fila3, fila4, col, sep);
			
		}
		
		System.out.println(col+ sep +  new String (fila1) + sep +  new String(fila2) + sep + new String(fila3) + sep + new String(fila4) + sep);
	}
		
}//fin de la clase
