package main;
//package lvl2advanced.p01gui.p01simple;

public class App {

    public static void main(String[] args) {
        new LwjglWindow(new Renderer
                (200        		/*nastav jemnost gridu, hodnota 1000 způsobí že GridFactory vytvoří grid1000x1000*/,
                        0        	/*nastav typ projekce: 0 - perspektivní, 1 - orthogonální*/,
                        1            /*nastav typ osvětlení: 0 - per pixel, 1 - per vertex*/,
                        true        /*zobraz objekt v kartezske soustave (1) - rovina*/,
                        false       /*zobraz objekt v kartezske soustave (2) - oscilující rovina*/,
                        true        /*zobraz objekt ve sfericke soustave (1) - bramboroid*/,
                        true        /*zobraz objekt ve sfericke soustave (2) - koule*/,
                        false     	/*zobraz objekt ve cylindricke soustave (1) - válec*/,
                        false      	/*zobraz objekt ve cylindricke soustave (2) - mořská mušle*/
                ));
    }

}
