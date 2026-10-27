package de.emesit.demo.i18n;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

public class SimpleTranslationServiceTest  {

    @Test
    void testGetTranslation() {
        SimpleTranslationService translationService = new SimpleTranslationService();
        
        assertNull(translationService.getTranslation((Language)null, (String)null, (Object[])null));
        assertNull(translationService.getTranslation(Language.DE, (String)null, (Object[])null));
        assertNull(translationService.getTranslation((Language)null, "", (Object[])null));
        
        assertNull(translationService.getTranslation((Language)null, (String)null, (String[])null));
        assertNull(translationService.getTranslation(Language.DE, (String)null, (String[])null));
        assertNull(translationService.getTranslation((Language)null, "", (String[])null));
        
        assertNull(translationService.getTranslation(Language.DE, "", (List<String>)null));
        assertNull(translationService.getTranslation(Language.DE, "", List.of()));
        
        
        String   k = "MY_KEY";
        Language l = Language.DE;
        
        assertNull(translationService.getTranslation(l, k, (String[])null));
        
        String t = "Mein Schlüssel";
        translationService.setTranslation(k, l, t);
        assertEquals(t, translationService.getTranslation(l, k, (String[])null));
        assertEquals(t, translationService.getTranslation(l, k, new String[] {}));
        assertNull(translationService.getTranslation(l, "WRONG_KEY", (String[])null));
        assertNull(translationService.getTranslation(Language.ES, "WRONG_KEY", (String[])null));
        
        t = "mit Param {0}";
        translationService.setTranslation(k, l, t);
        assertEquals(t, translationService.getTranslation(l, k, (String[])null));
        assertEquals(t, translationService.getTranslation(l, k, new String[] {}));
        assertEquals("mit Param ppp", translationService.getTranslation(l, k, new String[] {"ppp"}));
        
        t = "andere Übersetzung";
        translationService.setTranslation(k, l, t);
        assertEquals(t, translationService.getTranslation(l, k, (String[])null));
        
        
        Map<I18nLanguage, String> mapWithEmptyTranslation = Map.of(Language.DE, "");
        assertNull(translationService.getTranslation(mapWithEmptyTranslation, Language.DE, null));
    }
    
    @Test
    void testSetTranslation() {
        SimpleTranslationService translationService = new SimpleTranslationService();
        assertEquals(0, translationService.keySize());
        assertEquals(0, translationService.totalSize());

        translationService.setTranslation(null, (Language)null, "bla");
        translationService.setTranslation(""  , (Language)null, "bla");
        translationService.setTranslation(null,  Language.DE  , "bla");
        translationService.setTranslation(""  ,  Language.DE  , "bla");
        translationService.setTranslation(null, (Language)null,  null);
        translationService.setTranslation(""  , (Language)null,  null);
        translationService.setTranslation(null,  Language.DE  ,  null);
        translationService.setTranslation(""  ,  Language.DE  ,  null);
        translationService.setTranslation(null, (Language)null,  ""  );
        translationService.setTranslation(""  , (Language)null,  ""  );
        translationService.setTranslation(null,  Language.DE  ,  ""  );
        translationService.setTranslation(""  ,  Language.DE  ,  ""  );
        translationService.setTranslation("K1", (Language)null,  null);
        translationService.setTranslation("K1", (Language)null,  ""  );
        translationService.setTranslation("K1",  Language.DE  ,  null);
        translationService.setTranslation("K1",  Language.DE  ,  ""  );
        assertEquals(0, translationService.keySize());
        assertEquals(0, translationService.totalSize());
        
        translationService.setTranslation("K1",  Language.DE  , "bla");
        assertEquals(1, translationService.keySize());
        assertEquals(1, translationService.totalSize());
        
        translationService.setTranslation("K1",  Language.EN  , "foo");
        assertEquals(1, translationService.keySize());
        assertEquals(2, translationService.totalSize());
        
        translationService.setTranslation("K1",  Language.DE  , "überschrieben");
        assertEquals(1, translationService.keySize());
        assertEquals(2, translationService.totalSize());
    }
    
    @Test
    void testRemoveTranslation() {
        SimpleTranslationService translationService = new SimpleTranslationService();

        assertTrue( translationService.setTranslation("K1",  Language.EN  , "foo") );
        assertTrue( translationService.setTranslation("K1",  Language.DE  , "bla") );
        assertEquals(1, translationService.keySize());
        assertEquals(2, translationService.totalSize());
        assertEquals("foo", translationService.getTranslation(Language.EN, "K1", (Object[])null));
        assertEquals("bla", translationService.getTranslation(Language.DE, "K1", (Object[])null));
        
        assertTrue( translationService.setTranslation("K1",  Language.EN  , null) );
        assertNull(translationService.getTranslation(Language.EN, "K1", (Object[])null));
        assertEquals("bla", translationService.getTranslation(Language.DE, "K1", (Object[])null));
        
        assertTrue( translationService.setTranslation("K1",  Language.DE, null) );
        assertNull(translationService.getTranslation(Language.DE, "K1", (Object[])null));
        
        // assert that removing not existing translation does not throw an exception but returns false
        assertFalse( translationService.setTranslation("K1",  Language.DE, null) );
    }
    
    @Test
    void testGetRawTranslationMap() {
        SimpleTranslationService translationService = new SimpleTranslationService();

        translationService.setTranslation("K1",  Language.EN  , "foo");
        translationService.setTranslation("K1",  Language.DE  , "bla");
        translationService.setTranslation("K2",  Language.DE  , "bla of {0}");
        
        assertEquals("bla of {0}", translationService.getTranslation( Language.DE, "K2", (Object[])null ));
        assertEquals("bla of "   , translationService.getTranslation( Language.DE, "K2", List.of("")    ));
        assertEquals("bla of p"  , translationService.getTranslation( Language.DE, "K2", List.of("p")   ));
        
        Map<String,Map<I18nLanguage,String>> rawTranslations = translationService.getRawTranslations();
        assertEquals("bla of {0}", rawTranslations.get("K2").get(Language.DE));
    }
}
