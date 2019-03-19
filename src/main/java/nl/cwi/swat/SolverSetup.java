package nl.cwi.swat;

import dagger.Component;
import nl.cwi.swat.formulacircuit.FormulaFactory;
import nl.cwi.swat.translation.TranslationCache;
import nl.cwi.swat.translation.Translator;

import javax.inject.Singleton;

@Singleton
@Component(modules = Config.class)
public interface SolverSetup {
  Translator translator();
  TranslationCache translationCache();

  FormulaFactory formulaFactory();
}
