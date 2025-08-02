package ca.bcit.comp2522.termProject.WordGame;

/**
 * Represents a country with its name, capital city, and associated facts.
 * Ensures that the provided data is valid during instantiation.
 *
 * @author Pranjal Sharma [A01396406]
 * @version 1.0
 */
public class Country
{
    private final String     name;
    private final String     capitalCityName;
    private final String[]   facts;
    private static final int MIN_FACTS_REQUIRED = 3;

    /**
     * Constructs a Country instance with its name, capital city, and facts.
     *
     * @param name the name of the country
     * @param capitalCityName the name of the capital city
     * @param facts an array of interesting facts about the country
     */
    public Country(final String name,
                   final String capitalCityName,
                   final String[] facts)
    {
        if(!isValidName(name))
        {
            throw new IllegalArgumentException("Invalid country name");
        }

        if(!isValidCapital(capitalCityName))
        {
            throw new IllegalArgumentException("Invalid capital city name");
        }

        if(!isValidFacts(facts))
        {
            throw new IllegalArgumentException("Invalid facts array");
        }

        this.name            = name;
        this.capitalCityName = capitalCityName;
        this.facts           = facts;
    }

    /**
     * Retrieves the name of the country.
     *
     * @return the name of the country
     */
    public String getName()
    {
        return name;
    }

    /**
     * Retrieves the name of the capital city.
     *
     * @return the name of the capital city
     */
    public String getCapitalCityName()
    {
        return capitalCityName;
    }

    /**
     * Retrieves the array of facts about the country.
     *
     * @return an array of facts about the country
     */
    public String[] getFacts()
    {
        return facts;
    }

    /*
     * Validates the country name to ensure it is non-null and not empty.
     */
    private static boolean isValidName(final String name)
    {

        return name != null && !name.trim().isEmpty();
    }

    /*
     * Validates the capital city name to ensure it is non-null and not empty.
     */
    private static boolean isValidCapital(final String capitalCityName)
    {
        return capitalCityName != null && !capitalCityName.trim().isEmpty();
    }

    /*
     * Validates the facts array to ensure it is non-null and contains at least
     * the minimum required facts.
     */
    private static boolean isValidFacts(final String[] facts)
    {
        return facts != null && facts.length >= MIN_FACTS_REQUIRED;
    }
}
