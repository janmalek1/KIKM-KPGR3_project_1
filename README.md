Aplikace vykresluje pouze jednu scénu.
Veškerá nastavení, co má v této scéně být a další parametry zobrazení, se dělají ve třídě App.

Je zde možno nastavit následující:
	gridGranularity		/*nastav jemnost gridu, hodnota 1000 způsobí že GridFactory vytvoří grid1000x1000*/,
	projectionType 		/*nastav typ projekce: 0 - perspektivní, 1 - orthogonální*/,
	shaderType			/*nastav typ osvětlení: 0 - per pixel, 1 - per vertex*/,
	showObject1 		/*zobraz objekt v kartezske soustave (1) - rovina*/,
	showObject2			/*zobraz objekt v kartezske soustave (2) - oscilující rovina*/,
	showObject3 		/*zobraz objekt ve sfericke soustave (1) - bramboroid*/,
	showObject4			/*zobraz objekt ve sfericke soustave (2) - koule*/,
	showObject5			/*zobraz objekt ve cylindricke soustave (1) - válec*/,
	showObject6 		/*zobraz objekt ve cylindricke soustave (2) - mořská mušle*/

-Defaultně aplikace vzkresluje statický bramboroid (showObject3 = true) + kroužící kouli (showObject4 = true) a rovinu (showObject1 = true). Je možné nastavit i zobrazování dalších objektů, akorát scéna začne být lehce nepřehledná.
-perspektivní/orthogonální projekce se nastaví pomocí projectionType
-osvětlení perVertex/perPixel se nastaví pomocí shaderType


S pozdravem
Jan Málek
janmalek@centrum.cz, malekja1@uhk.cz