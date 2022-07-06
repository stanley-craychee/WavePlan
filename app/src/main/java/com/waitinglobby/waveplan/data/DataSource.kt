package com.waitinglobby.waveplan.data

import com.waitinglobby.waveplan.R
import com.waitinglobby.waveplan.model.*

/**
 * An object to generate a static list of beaches
 */
object DataSource {

    // Control table for Continents
    val continents: List<Continent> = listOf(
        Continent(0, "Africa"),
        Continent(1, "Asia"),
        Continent(2, "Europe"),
        Continent(3, "North America"),
        Continent(4, "Oceania"),
        Continent(5, "South America"),
    )

    // Control table for Countries
    val countries: List<Country> = listOf(
        Country(0, "South Africa", 0),
        Country(1, "Japan", 1),
        Country(2, "Portugal", 2),
        Country(3, "United States", 3),
        Country(4, "Australia", 4),
        Country(5, "Brazil", 5),
        Country(6, "Morocco", 0),
        Country(7, "Maldives", 1),
        Country(8, "Ireland", 2),
    )

    // Control table for States
    val states: List<State> = listOf(
        State(0, "Western Cape", 0),
        State(1, "Miyazaki", 1),
        State(2, "Leiria", 2),
        State(3, "California", 3),
        State(4, "Western Australia", 4),
        State(5, "Rio de Janeiro", 5),
        State(6, "Souss-Massa", 6),
        State(7, "Eastern Cape", 0),
        State(8, "Kaafu Atoll", 7),
        State(9, "Connaught", 8),

        )

    // Control table for Cities
    val cities: List<City> = listOf(
        City(0, "City of Cape Town", 0),
        City(1, "Kushima Shi", 1),
        City(2, "Peniche Municipality", 2),
        City(3, "San Francisco", 3),
        City(4, "Denmark", 4),
        City(5, "Saquarema", 5),
        City(6, "San Diego", 3),
        City(7, "Half Moon Bay", 3),
        City(8, "Agadir-Ida-ou-Tnan", 6),
        City(9, "Buffalo City Metropolitan Municipality", 7),
        City(10, "Hulhumale", 8),
        City(11, "County Leitrim", 9),
    )

    // Master list of all Beaches with referential linkage to control tables from above
    val beaches: List<Beach> = listOf(
        Beach(
            0,
            R.drawable.ocean_beach,
            "Ocean Beach",
            3,
            3,
            3,
            37.788,
            -122.632,
        ),
        Beach(
            1,
            R.drawable.mavericks,
            "Mavericks",
            3,
            3,
            7,
            37.496,
            -122.497,
        ),
        Beach(
            2,
            R.drawable.blacks_beach,
            "Blacks Beach",
            3,
            3,
            6,
            32.891,
            -117.254,
        ),
        Beach(
            3,
            R.drawable.dungeons,
            "Dungeons",
            0,
            0,
            0,
            -34.061,
            18.324,
        ),
        Beach(
            4,
            R.drawable.wave_placeholder,
            "Nagata",
            1,
            1,
            1,
            30.407,
            130.434,
        ),
        Beach(
            5,
            R.drawable.supertubos,
            "Supertubos",
            2,
            2,
            2,
            39.341,
            9.361,
        ),
        Beach(
            6,
            R.drawable.the_right,
            "The Right",
            4,
            4,
            4,
            -34.414,
            115.097,
        ),
        Beach(
            7,
            R.drawable.saquarema,
            "Saquarema",
            5,
            5,
            5,
            -22.971,
            -42.499,
        ),
        Beach(
            8,
            R.drawable.sunset_reef,
            "Sunset Reef",
            0,
            0,
            0,
            -34.126,
            18.332,
        ),
        Beach(
            9,
            R.drawable.llandudno,
            "Llandudno",
            0,
            0,
            0,
            -34.007,
            18.340,
        ),
        Beach(
            10,
            R.drawable.killers,
            "Killers",
            6,
            6,
            8,
            30.407,
            -9.604,
        ),
        Beach(
            11,
            R.drawable.anchor_point,
            "Anchor point",
            6,
            6,
            8,
            30.544,
            -9.725,
        ),
        Beach(
            12,
            R.drawable.nahoon_reef,
            "Nahoon Reef",
            6,
            7,
            9,
            -32.996,
            27.953,
        ),
        Beach(
            13,
            R.drawable.jailbreaks,
            "Jailbreaks",
            7,
            8,
            10,
            -32.996,
            27.953,
        ),
        Beach(
            14,
            R.drawable.mullaghmore,
            "Mullaghmore Head",
            8,
            9,
            11,
            54.477,
            -8.457,
        ),
        Beach(
            15,
            R.drawable.fort_point,
            "Fort Point",
            3,
            3,
            3,
            37.810,
            -122.475,
        ),
        Beach(
            16,
            R.drawable.cardiff,
            "Cardiff Reef",
            3,
            3,
            6,
            33.014,
            -117.283,
        )
    )
}