package org.eclipse.viatra2.patternlanguage.ui.labeling;

import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.viatra2.patternlanguage.EMFPatternLanguageScopeHelper;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.ExpressionConstraint;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.IntValue;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PathExpressionHead;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PathExpressionTail;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternBody;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternCompositionConstraint;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.StringValue;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.ValueReference;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.VariableValue;
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.ClassType;
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.EClassConstraint;
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.PackageImport;
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.PatternModel;
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.ReferenceType;
import org.eclipse.xtext.ui.label.DefaultEObjectLabelProvider;

import com.google.inject.Inject;

/**
 * Provides labels for a EObjects.
 * 
 * see http://www.eclipse.org/Xtext/documentation/latest/xtext.html#labelProvider
 */
public class EMFPatternLanguageLabelProvider extends DefaultEObjectLabelProvider {

	@Inject
	public EMFPatternLanguageLabelProvider(AdapterFactoryLabelProvider delegate) {
		super(delegate);
	}

	String text(PatternModel model) {
		return "Pattern Model";
	}
	
	String text(PackageImport ele) {
		String name = (ele.getEPackage() != null) ? ele.getEPackage().getName() : "«package»";
		return String.format("import %s", name);
	}
	
	String text(Pattern pattern) {
		return String.format("pattern %s/%d", pattern.getName(), pattern.getParameters().size());
	}
	
	String text(PatternBody ele) {
		return String.format("body #%d", ((Pattern)ele.eContainer()).getBodies().indexOf(ele) + 1);
	}
	
	String text(EClassConstraint constraint) {
		String typename = ((ClassType)constraint.getType()).getClassname().getName();
		return String.format("%s (%s)", typename, constraint.getVar().getVar());
	}
	
	String text(PatternCompositionConstraint constraint) {
		String modifiers = (constraint.isNegative()) ? "neg " : "";
		return String.format("find %s%s/%d", modifiers, constraint.getPatternRef().getName(), constraint.getParameters().size());
	}
	
	String text(ExpressionConstraint constraint) {
		String typename = ((ClassType)constraint.getHead().getType()).getClassname().getName();
		return String.format("%s (%s)", typename, constraint.getHead().getSrc().getVar());
	}
	
	String text(PathExpressionTail tail) {
		String type = ((ReferenceType)tail.getType()).getRefname().getName();
		String varName = "";
		if (tail.getTail() == null) {
			PathExpressionHead head = EMFPatternLanguageScopeHelper.getExpressionHead(tail);
			varName = String.format("(%s)",getValueText(head.getDst()));
		}
		return String.format("%s %s",type, varName);
	}
	
	String getValueText(ValueReference ref) {
		if (ref instanceof VariableValue) {
			return ((VariableValue) ref).getValue().getVar();
		} else if (ref instanceof IntValue) {
			return Integer.toString(((IntValue) ref).getValue());
		} else if (ref instanceof StringValue) {
			return ((StringValue) ref).getValue();
		}
		return null;
	}
	
/*
	//Labels and icons can be computed like this:
	
	String text(MyModel ele) {
	  return "my "+ele.getName();
	}
	 
    String image(MyModel ele) {
      return "MyModel.gif";
    }
*/
}
