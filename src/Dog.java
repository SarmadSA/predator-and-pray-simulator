
import java.util.HashMap;
import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A simple model of a fox. Foxes age, move, eat rabbits, and die.
 *
 * @author David J. Barnes and Michael Kölling
 * @version 2016.02.29 (2)
 */
public class Dog extends Animal {
    // Characteristics shared by all foxes (class variables).

    // The age at which a fox can start to breed.
    private static final int BREEDING_AGE = 300;
    // The age to which a fox can live.
    //private static final int MAX_AGE = 150;
    // The likelihood of a fox breeding.
    private static final double BREEDING_PROBABILITY = 0.1;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 5;
    // The food value of a single rabbit. In effect, this is the
    // number of steps a fox can go before it has to eat again.
    private static final int CAT_FOOD_VALUE = 20;
    private static final int MOUSE_FOOD_VALUE = 10;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();

    private int maxSpeed = 5;

    // Individual characteristics (instance fields).
    // The fox's age.
    private int age;
    // The fox's food level, which is increased by eating rabbits.
    private int foodLevel;

    private int MAX_AGE;
    
    private String name = "Dog";
    
    private HashMap<String, String> dogData = new HashMap<>();


    /**
     * Create a fox. A fox can be created as a new born (age zero and not
     * hungry) or with a random age and food level.
     *
     * @param randomAge If true, the fox will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Dog(boolean randomAge, Field field, Location location) {
        super(field, location);
        if (randomAge) {
            MAX_AGE = rand.nextInt(5400) + 600;
            age = rand.nextInt(MAX_AGE);
            foodLevel = CAT_FOOD_VALUE;
        } else {
            MAX_AGE = rand.nextInt(5400) + 600;
            age = 0;
            foodLevel = CAT_FOOD_VALUE;
        }
    }

    /**
     * This is what the fox does most of the time: it hunts for rabbits. In the
     * process, it might breed, die of hunger, or die of old age.
     *
     * @param field The field currently occupied.
     * @param newFoxes A list to return newly born foxes.
     */
    public void act(List<Animal> newDogs) {
        incrementAge();
        incrementHunger();
        if (isAlive()) {
            giveBirth(newDogs);
            // Move towards a source of food if found.
            Location newLocation = findFood();

            for (int i = 0; i < rand.nextInt(maxSpeed) + 1; i++) {
                if (newLocation == null) {
                    // No food found - try to move to a free location.
                    newLocation = getField().freeAdjacentLocation(getLocation());
                }
            }
            // See if it was possible to move.
            if (newLocation != null) {
                setLocation(newLocation);
            } else {
                // Overcrowding.
                setDead(dogData, name, age);
            }
        }
    }

    /**
     * Increase the age. This could result in the fox's death.
     */
    private void incrementAge() {
        age++;
        if (age > MAX_AGE) {
            setDead(dogData, name, age);
        }
    }

    /**
     * Make this fox more hungry. This could result in the fox's death.
     */
    private void incrementHunger() {
        foodLevel--;
        if (foodLevel <= 0) {
            setDead(dogData, name, age);
        }
    }

    /**
     * Look for rabbits adjacent to the current location. Only the first live
     * rabbit is eaten.
     *
     * @return Where food was found, or null if it wasn't.
     */
    private Location findFood() {
        if (foodLevel <= 10) {
            Field field = getField();
            List<Location> adjacent = field.adjacentLocations(getLocation());
            Iterator<Location> it = adjacent.iterator();
            while (it.hasNext()) {
                Location where = it.next();
                Object animal = field.getObjectAt(where);
                if (animal instanceof Cat) {
                    Cat cat = (Cat) animal;
                    if (cat.isAlive()) {
                        cat.setDead(cat.catData, cat.name, cat.age);
                        foodLevel += CAT_FOOD_VALUE;
                        return where;
                    }
                } else if (animal instanceof Mouse) {
                    Mouse mouse = (Mouse) animal;
                    if (mouse.isAlive()) {
                        mouse.setDead(mouse.mouseData, mouse.name, mouse.age);
                        foodLevel += MOUSE_FOOD_VALUE;
                        return where;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Check whether or not this fox is to give birth at this step. New births
     * will be made into free adjacent locations.
     *
     * @param newFoxes A list to return newly born foxes.
     */
    private void giveBirth(List<Animal> newDogs) {
        // New foxes are born into adjacent locations.
        // Get a list of adjacent free locations.

        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for (int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Dog young = new Dog(false, field, loc);
            newDogs.add(young);
        }
    }

    /**
     * Generate a number representing the number of births, if it can breed.
     *
     * @return The number of births (may be zero).
     */
    private int breed() {
        int births = 0;
        if (canBreed() && rand.nextDouble() <= BREEDING_PROBABILITY) {
            births = rand.nextInt(MAX_LITTER_SIZE) + 1;
        }
        return births;
    }

    /**
     * A fox can breed if it has reached the breeding age.
     */
    private boolean canBreed() {
        return age >= BREEDING_AGE;
    }

    private void debuff() {
        if (foodLevel <= 2) {
            maxSpeed -= 2;
        }
    }
}
