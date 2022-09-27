package utils.capital;

/**
 * Capital.
 *
 * @author Lucas Stadelmann
 * @since 20.11.30
 */
public enum Capital {

    DEP_0("", "France"),
    DEP_1("1", "Bourg-en-Bresse"),
    DEP_2("2", "Laon"),
    DEP_3("3", "Moulins"),
    DEP_4("4", "Digne-les-Bains"),
    DEP_5("5", "Gap"),
    DEP_6("6", "Nice"),
    DEP_7("7", "Privas"),
    DEP_8("8", "Charleville-Mézières"),
    DEP_9("9", "Foix"),
    DEP_10("10", "Troyes"),
    DEP_11("11", "Carcassonne"),
    DEP_12("12", "Rodez"),
    DEP_13("13", "Marseille"),
    DEP_14("14", "Caen"),
    DEP_15("15", "Aurillac"),
    DEP_16("16", "Angoulême"),
    DEP_17("17", "La Rochelle"),
    DEP_18("18", "Bourges"),
    DEP_19("19", "Tulle"),
    DEP_2A("2A", "Ajaccio"),
    DEP_2B("2B", "Bastia"),
    DEP_21("21", "Dijon"),
    DEP_22("22", "St-Brieuc"),
    DEP_23("23", "Guéret"),
    DEP_24("24", "Périgueux"),
    DEP_25("25", "Besançon"),
    DEP_26("26", "Valence"),
    DEP_27("27", "Évreux"),
    DEP_28("28", "Chartres"),
    DEP_29("29", "Quimper"),
    DEP_30("30", "Nîmes"),
    DEP_31("31", "Toulouse"),
    DEP_32("32", "Auch"),
    DEP_33("33", "Bordeaux"),
    DEP_34("34", "Montpellier"),
    DEP_35("35", "Rennes"),
    DEP_36("36", "Châteauroux"),
    DEP_37("37", "Tours"),
    DEP_38("38", "Grenoble"),
    DEP_39("39", "Lons-le-Saunier"),
    DEP_40("40", "Mont-de-Marsan"),
    DEP_41("41", "Blois"),
    DEP_42("42", "Saint-Étienne"),
    DEP_43("43", "Le Puy-en-Velay"),
    DEP_44("44", "Nantes"),
    DEP_45("45", "Orléans"),
    DEP_46("46", "Cahors"),
    DEP_47("47", "Agen"),
    DEP_48("48", "Mende"),
    DEP_49("49", "Angers"),
    DEP_50("50", "Saint-Lô"),
    DEP_51("51", "Châlons-en-Champagne"),
    DEP_52("52", "Chaumont"),
    DEP_53("53", "Laval"),
    DEP_54("54", "Nancy"),
    DEP_55("55", "Bar-le-Duc"),
    DEP_56("56", "Vannes"),
    DEP_57("57", "Metz"),
    DEP_58("58", "Nevers"),
    DEP_59("59", "Lille"),
    DEP_60("60", "Beauvais"),
    DEP_61("61", "Alençon"),
    DEP_62("62", "Arras"),
    DEP_63("63", "Clermont-Ferrand"),
    DEP_64("64", "Pau"),
    DEP_65("65", "Tarbes"),
    DEP_66("66", "Perpignan"),
    DEP_67("67", "Strasbourg"),
    DEP_68("68", "Colmar"),
    DEP_69("69", "Lyon"),
    DEP_70("70", "Vesoul"),
    DEP_71("71", "Mâcon"),
    DEP_72("72", "Le Mans"),
    DEP_73("73", "Chambéry"),
    DEP_74("74", "Annecy"),
    DEP_75("75", "Paris"),
    DEP_76("76", "Rouen"),
    DEP_77("77", "Melun"),
    DEP_78("78", "Versailles"),
    DEP_79("79", "Niort"),
    DEP_80("80", "Amiens"),
    DEP_81("81", "Albi"),
    DEP_82("82", "Montauban"),
    DEP_83("83", "Toulon"),
    DEP_84("84", "Avignon"),
    DEP_85("85", "La Roche-sur-Yon"),
    DEP_86("86", "Poitiers"),
    DEP_87("87", "Limoges"),
    DEP_88("88", "Épinal"),
    DEP_89("89", "Auxerre"),
    DEP_90("90", "Belfort"),
    DEP_91("91", "Évry"),
    DEP_92("92", "Nanterre"),
    DEP_93("93", "Bobigny"),
    DEP_94("94", "Créteil"),
    DEP_95("95", "Pontoise"),
    DEP_971("971", "Basse-Terre"),
    DEP_972("972", "Fort-de-France"),
    DEP_973("973", "Cayenne"),
    DEP_974("974", "Saint-Denis"),
    DEP_975("975", "Saint-Pierre"),
    DEP_976("976", "Mamoudzou"),
    DEP_977("977", "Gustavia"),
    DEP_978("978", "Marigot"),
    DEP_986("986", "Matā'utu"),
    DEP_987("987", "Papeete"),
    DEP_988("988", "Nouméa");

    private final String departmentNumber;

    private final String capitalName;

    Capital(final String departmentNumber,
            final String capitalName) {
        this.departmentNumber = departmentNumber;
        this.capitalName = capitalName;
    }

    public static Capital resolveCapital(final String departmentNumber) {
        for (final Capital capital : Capital.values()) {
            if (capital.departmentNumber.equals(departmentNumber)) {
                return capital;
            }
        }
        return Capital.DEP_0;
    }

    public String getDepartmentNumber() {
        return this.departmentNumber;
    }

    public String getCapitalName() {
        return this.capitalName;
    }
}
