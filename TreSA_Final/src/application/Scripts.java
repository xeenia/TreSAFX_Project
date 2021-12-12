package application;

import java.io.IOException;


public class Scripts {
	static void Script(int choise) throws IOException {
		if(choise == 1) {
			System.out.println("\n\nKALOSORISES STHN TReSA\n\nOI EPILOGES SOU EINAI:");
			System.out.println("1. Plithos arxeiwn sto Data Dir\n2. Dimiourgia h enhmerwsh euretiriou\n3. Anazhthsh leksis\n4. Exit");
		}
		else if(choise == 2) {
			System.out.println("\n\n");
		}
		else if(choise == 3) {
			System.out.println("\nHave a nice day!");
		}
		else if(choise == 4) {
			System.out.println("\n\nLathos kwdikos");
		}
		else if(choise == 5) {
			System.out.println("\n\nTo arxeio sou dimiourgithike");
		}
		else if(choise == 6) {
			System.out.println("\nSe poia xwra exei graftei:");
		}
		else if(choise == 7) {
			System.out.println("\nApo poious exei graftei[mporeis na to afhseis kai keno]:");
		}
		else if(choise == 8) {
			System.out.println("\nPoios einai o titlos tous arthrou:");
		}
		else if(choise == 9) {
			System.out.println("\nPoia einai ta periexomena tou arthrou:");
		}
		else if(choise == 10) {
			System.out.println("\nPoion arithmo arxeiou thes na diagrapseis:");
		}
		else if(choise == 11) {
			System.out.println("\nTi theleis na kanei:");
		}
		else if(choise == 12) {
			System.out.println("\nTo euretirio dimiourgithike!");
		}
		else if(choise == 13) {
			System.out.println("\nGrapse thn leksi pou thes na breis:");
		}
		else if(choise == 14) {
			System.out.println("\n\nEpelekse:\n1. Dimiourgia/Epanadimioiurgia index\n2. Eisagwgh arxeioy\n3. Diagrafh arxeioy\n4. Back");
		}
		else if(choise == 15) {
			System.out.println("Epelekse kapoio arxeio px Article8.txt Article10.txt:\n");
		}
		else if(choise == 16) {
			System.out.println("\nTa arxeia mesa sto DataDir einai:\n");
		}
		else if(choise == 17) {
			System.out.println("\nThes o Index na einai apo tin arxi:\n1. Gematos(ola ta arxeia tou Dir Data)\n2. Adeios\n");
		}
		else if(choise == 18) {
			System.out.println("\nEidos anazhthshs:\n1. Query Parser\n2. Boolean Query\n3. Phrase Query");
		}
		else if(choise == 19) {
			System.out.println("\nGia na ginei swsta to Boolean Model prepei na grafeis:\nKsexarismos me parenthesis anamesa stis " + 
							   "logikes prakseis\n&& <- AND\n|| <- OR\n^ <- NOT\n\n");
		}
		
	}
	
	static void ScriptWithString(int choise, String s) {
		if(choise == 1) {
			System.out.println("Incorrect file name: "+ s);
		}
		if(choise == 2) {
			System.out.println("Eisixthike to arxeio noume "+s + " !!");
		}
	}
	
	static void ScriptWithNum(int choise, int i) {
		// empty
	}
}