public class IntersectionWithoutSemaphore {

    public static void main(String[] args) {
        // Créer deux threads représentant deux voitures
        Thread car1 = new Thread(new Car("Voiture 1"));
        Thread car2 = new Thread(new Car("Voiture 2"));

        // Démarrer les threads
        car1.start();
        car2.start();
    }

    static class Car implements Runnable {
        private final String name;

        public Car(String name) {
            this.name = name;
        }

        @Override
        public void run() {
            while (true) { // Boucle infinie jusqu'à collision
                System.out.println(name + " approche de l'intersection.");

                // Simuler le temps d'approche de l'intersection
                try {
                    Thread.sleep(1000); // Temps pour atteindre l'intersection
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // Traverser l'intersection
                System.out.println(name + " commence à traverser l'intersection.");

                // Simuler le temps de traversée de l'intersection
                try {
                    Thread.sleep(2000); // Temps pour traverser l'intersection
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // Vérifier si une autre voiture est en train de traverser
                if (intersectionOccupied) {
                    System.out.println(
                            "Il y a un accident!!!!! " + name + " est entré en collision avec une autre voiture.");
                    System.exit(1); // Arrêter le programme en cas d'accident
                }

                // Marquer l'intersection comme occupée
                intersectionOccupied = true;

                System.out.println(name + " a traversé l'intersection en toute sécurité.");

                // Marquer l'intersection comme libre
                intersectionOccupied = false;

                // Attendre un peu avant de recommencer
                try {
                    Thread.sleep(1000); // Temps avant de réessayer
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        // Variable partagée pour indiquer si l'intersection est occupée
        private static boolean intersectionOccupied = false;
    }
}