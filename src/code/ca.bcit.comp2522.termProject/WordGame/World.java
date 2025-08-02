package ca.bcit.comp2522.termProject.WordGame;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Represents a collection of countries and their associated data.
 * The countries are loaded from text files located in a resources directory.
 * Each file is named after a letter of the alphabet and contains data about
 * one or more countries.
 *
 * @author Pranjal Sharma [A01396406]
 * @version 2.0
 */
public class World
{
    private static final char          FIRST_ALPHABET         = 'a';
    private static final char          LAST_ALPHABET          = 'z';
    private static final int           MIN_REQUIRED_LINES     = 2;
    private static final int           NAME_AND_CAPITAL_INDEX = 0;
    private static final int           NAME_AND_CAPITAL_PARTS = 2;
    private static final int           NAME_INDEX             = 0;
    private static final int           CAPITAL_INDEX          = 1;
    private static final int           FACTS_START_INDEX      = 1;
    private final Map<String, Country> countries;
    private static Path                filePath;

    /**
     * Initializes a new World instance and loads country data from resources.
     */
    public World()
    {
        countries = new HashMap<>();
        loadCountries();
    }

    /*
     * Loads country data from text files.
     * Each file corresponds to a letter of the alphabet and contains data about countries.
     */
    private void loadCountries()
    {
        for(char alphabet = FIRST_ALPHABET; alphabet <= LAST_ALPHABET; alphabet++)
        {
            filePath = Paths.get("src/resources/" + alphabet + ".txt");

            if(Files.exists(filePath))
            {
                try
                {
                    final List<String> lines;
                    final List<String> currentCountryData;

                    lines = Files.readAllLines(filePath);
                    currentCountryData = new ArrayList<>();

                    for(String line : lines)
                    {
                        line = line.trim();

                        if(line.isEmpty())
                        {
                            // Process the accumulated data for one country
                            if(!currentCountryData.isEmpty())
                            {
                                addCountryFromData(currentCountryData);
                                currentCountryData.clear();
                            }
                        }
                        else
                        {
                            currentCountryData.add(line);
                        }
                    }
                    // Add the last country if there's no trailing blank line
                    if(!currentCountryData.isEmpty())
                    {
                        addCountryFromData(currentCountryData);
                    }
                }
                catch(final IOException e)
                {
                    System.out.println("Error reading file " + filePath +
                            ": " + e.getMessage());
                }
            }
        }
    }

    /*
     * Parses and adds a country's data to the collection.
     */
    private void addCountryFromData(final List<String> data)
    {
        if(data.size() < MIN_REQUIRED_LINES)
        {
            return; // Ensure there's enough data
        }

        final String[] nameAndCapital;
        final String name;
        final String capital;
        final String[] facts;

        nameAndCapital = data.get(NAME_AND_CAPITAL_INDEX).split(":");

        if(nameAndCapital.length != NAME_AND_CAPITAL_PARTS)
        {
            return; // Validate format
        }

        name    = nameAndCapital[NAME_INDEX].trim();
        capital = nameAndCapital[CAPITAL_INDEX].trim();
        facts   = new String[data.size() - FACTS_START_INDEX];

        for(int i = FACTS_START_INDEX; i < data.size(); i++)
        {
            facts[i - FACTS_START_INDEX] = data.get(i).trim();
        }

        try
        {
            final Country country;
            country = new Country(name, capital, facts);
            countries.put(name.toLowerCase(), country);
        }

        catch(final IllegalArgumentException e)
        {
            System.out.println("Invalid country data for " + name +
                    ": " + e.getMessage());
        }
    }

    /**
     * Retrieves a country by its name.
     *
     * @param name the name of the country
     * @return the Country object if found, or null if the country does not exist
     */
    public Country getCountryByName(final String name)
    {
        return countries.get(name.toLowerCase());
    }

    /**
     * Retrieves all countries in the collection.
     *
     * @return a map of country names to Country objects
     */
    public Map<String, Country> getAllCountries()
    {
        return countries;
    }
}
