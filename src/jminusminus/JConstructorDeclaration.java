// Copyright 2013 Bill Campbell, Swami Iyer and Bahar Akbal-Delibas

package jminusminus;

import java.util.ArrayList;
import static jminusminus.CLConstants.*;

/**
 * The AST node for a constructor declaration. A constructor looks very much
 * like a method.
 *
 * @see JMethodDeclaration
 */

class JConstructorDeclaration extends JMethodDeclaration {

    /** Does this constructor invoke this(...) or super(...)? */
    private boolean invokesConstructor;

    /** Defining class */
    JClassDeclaration definingClass;

    /**
     * Constructs an AST node for a constructor declaration given the line number,
     * modifiers, constructor name, formal parameters, and the constructor body.
     *
     * @param line           line in which the constructor declaration occurs in the
     *                       source file.
     * @param mods           modifiers.
     * @param name           constructor name.
     * @param params         the formal parameters.
     * @param exceptionTypes the exception types.
     * @param body           constructor body.
     */
    public JConstructorDeclaration(int line, ArrayList<String> mods, String name, ArrayList<JFormalParameter> params,
            ArrayList<Type> exceptionTypes, JBlock body) {
        super(line, mods, name, Type.CONSTRUCTOR, params, exceptionTypes, body);
    }

    /**
     * Declares this constructor in the parent (class) context.
     *
     * @param context the parent (class) context.
     * @param partial the code emitter (basically an abstraction for producing the
     *                partial class).
     */

    public void preAnalyze(Context context, CLEmitter partial) {
        super.preAnalyze(context, partial);
        if (isStatic) {
            JAST.compilationUnit.reportSemanticError(line(), "Constructor cannot be declared static");
        } else if (isAbstract) {
            JAST.compilationUnit.reportSemanticError(line(), "Constructor cannot be declared abstract");
        }

        if (body.statements().size() > 0 && body.statements().get(0) instanceof JStatementExpression) {
            JStatementExpression first = (JStatementExpression) body.statements().get(0);
            if (first.expr instanceof JSuperConstruction) {
                ((JSuperConstruction) first.expr).markProperUseOfConstructor();
                invokesConstructor = true;
            } else if (first.expr instanceof JThisConstruction) {
                ((JThisConstruction) first.expr).markProperUseOfConstructor();
                invokesConstructor = true;
            }
        }
    }

    /**
     * Analysis for a constructor declaration is very much like that for a method
     * declaration.
     *
     * @param context context in which names are resolved.
     * @return the analyzed (and possibly rewritten) AST subtree.
     */

    public JAST analyze(Context context) {
        // Record the defining class declaration.
        definingClass = (JClassDeclaration) (context.classContext().definition());

        MethodContext methodContext = new MethodContext(context, isStatic, returnType, exceptionTypes, true);
        this.context = methodContext;

        if (!isStatic) {
            // Offset 0 is used to address "this"
            this.context.nextOffset();
        }

        // Declare the parameters. We consider a formal parameter
        // to be always initialized, via a function call.
        for (JFormalParameter param : params) {
            int currentOffest = this.context.nextOffset();
            if (param.type() == Type.DOUBLE) {
                // increase the offset because a double occupies 2 words
                this.context.nextOffset();
            }
            // we give the offset upon entering the method
            LocalVariableDefn defn = new LocalVariableDefn(param.type(), currentOffest);
            defn.initialize();
            this.context.addEntry(param.line(), param.name(), defn);
        }

        if (exceptionTypes != null) {
            int j = exceptionTypes.size(), i = 0;

            while (i < j) {
                if (Throwable.class.isAssignableFrom(exceptionTypes.get(i).classRep()))
                    this.context.addThownType(exceptionTypes.get(i));
                else
                    JAST.compilationUnit.reportSemanticError(line(), "must be Throwable or a subclass");

                i++;
            }
        }

        if (body != null) {
            body = body.analyze(this.context);
        }
        return this;

    }

    /**
     * Adds this constructor declaration to the partial class.
     *
     * @param context the parent (class) context.
     * @param partial the code emitter (basically an abstraction for producing the
     *                partial class).
     */

    public void partialCodegen(Context context, CLEmitter partial) {
        partial.addMethod(mods, "<init>", descriptor, null, false);
        if (!invokesConstructor) {
            partial.addNoArgInstruction(ALOAD_0);
            partial.addMemberAccessInstruction(INVOKESPECIAL,
                    ((JTypeDecl) context.classContext().definition()).superType().jvmName(), "<init>", "()V");
        }
        partial.addNoArgInstruction(RETURN);
    }

    /**
     * Generates code for the constructor declaration.
     *
     * @param output the code emitter (basically an abstraction for producing the
     *               .class file).
     */

    public void codegen(CLEmitter output) {
        output.addMethod(mods, "<init>", descriptor, null, false);
        if (!invokesConstructor) {
            output.addNoArgInstruction(ALOAD_0);
            output.addMemberAccessInstruction(INVOKESPECIAL,
                    ((JTypeDecl) context.classContext().definition()).superType().jvmName(), "<init>", "()V");
        }
        // Field initializations
        for (JFieldDeclaration field : definingClass.instanceFieldInitializations()) {
            field.codegenInitializations(output);
        }
        // And then the body
        body.codegen(output);
        output.addNoArgInstruction(RETURN);
    }

    /**
     * {@inheritDoc}
     */

    public void writeToStdOut(PrettyPrinter p) {
        p.printf("<JConstructorDeclaration line=\"%d\" " + "name=\"%s\">\n", line(), name);
        p.indentRight();
        if (context != null) {
            context.writeToStdOut(p);
        }
        if (mods != null) {
            p.println("<Modifiers>");
            p.indentRight();
            for (String mod : mods) {
                p.printf("<Modifier name=\"%s\"/>\n", mod);
            }
            p.indentLeft();
            p.println("</Modifiers>");
        }
        if (params != null) {
            p.println("<FormalParameters>");
            for (JFormalParameter param : params) {
                p.indentRight();
                param.writeToStdOut(p);
                p.indentLeft();
            }
            p.println("</FormalParameters>");
        }

        if (!exceptionTypes.isEmpty()) {
            p.println("<Throws>");

            p.indentRight();
            for (Type _type : exceptionTypes)
                p.printf("<ExceptionType name=\"%s\"/>\n", _type.toString());
            p.indentLeft();

            p.println("</Throws>");
        }

        if (body != null) {
            p.println("<Body>");
            p.indentRight();
            body.writeToStdOut(p);
            p.indentLeft();
            p.println("</Body>");
        }
        p.indentLeft();
        p.println("</JConstructorDeclaration>");
    }

}
