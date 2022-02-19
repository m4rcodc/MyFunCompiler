package Visitor;

import leafs.*;
import nodes.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;

//Visitor per la generazione dell'albero sintattico (Formato XML)

public class SyntaxVisitor implements ISyntaxVisitor{

        DocumentBuilderFactory dbFactory;
        DocumentBuilder dBuilder;
        Document doc;

        public SyntaxVisitor() throws ParserConfigurationException {
            dbFactory = DocumentBuilderFactory.newInstance();
            dBuilder = dbFactory.newDocumentBuilder();
            doc = dBuilder.newDocument();
        }

        @Override
        public Element visit(ProgramNode programNode) {
            // root element
            Element programElement = doc.createElement(programNode.name);
            doc.appendChild(programElement);

            //ArrayList<VarDeclNode>
            if(programNode.nodeArrayList != null){
                Element varDeclElement = doc.createElement("VarDeclList");
                for(VarDeclNode declNodo: programNode.nodeArrayList) {
                    varDeclElement.appendChild(declNodo.accept(this));
                }
                programElement.appendChild(varDeclElement);
            }

            //ArrayList<FunNode>
            Element funListElement = doc.createElement("FunList");
            for(FunNode funNode: programNode.funNodeArrayList) {
                funListElement.appendChild(funNode.accept(this));
            }

            programElement.appendChild(funListElement);

            //BodyNode (main)
            programElement.appendChild(programNode.bodyNode.accept(this));

            return programElement;

        }

        @Override
        public Element visit(BodyNode bodyNode) {
            Element bodyElement = doc.createElement(bodyNode.name);

            //ArrayList<VarDecl>
                Element varDeclElement = doc.createElement("VarDeclList");
                bodyElement.appendChild(varDeclElement);
                for (VarDeclNode varDeclNode : bodyNode.vardecl) {
                    varDeclElement.appendChild(varDeclNode.accept(this));
                }

            //StatListNode
            if(bodyNode.stats != null) {
                    bodyElement.appendChild(bodyNode.stats.accept(this));
            }
            return bodyElement;
        }

        @Override
        public Element visit(VarDeclNode varDeclNode){
            Element varDeclElement = doc.createElement(varDeclNode.name);


            //TypeNode
            varDeclElement.appendChild(varDeclNode.type.accept(this));



            //ArrayList <IdInitNode>
            for(IdInitNode idInitNode: varDeclNode.identifiers){
                varDeclElement.appendChild(idInitNode.accept(this));
            }
            return varDeclElement;
        }

        @Override
        public Element visit(TypeNode typeNode){
            Element typeNodeElement = doc.createElement(typeNode.name);
            typeNodeElement.setAttribute("type",typeNode.type);

            return typeNodeElement;
        }

        @Override
        public Element visit(IdInitNode idInitNode){
            Element idInitElement = doc.createElement(idInitNode.name);

            //LeafID
            idInitElement.appendChild(idInitNode.leaf.accept(this));

            //ExprNode
            if(idInitNode.expr != null){
                idInitElement.appendChild(idInitNode.expr.accept(this));
            }

            //ConstNode
            if(idInitNode.c != null) {
                idInitElement.appendChild(idInitNode.c.accept(this));
            }
            return idInitElement;
        }

        @Override
        public Element visit(ConstNode constNode){
            Element constElement = doc.createElement(constNode.name);

            if(constNode.value1 instanceof LeafIntegerConst)
                constElement.appendChild(((LeafIntegerConst)constNode.value1).accept(this));
            if(constNode.value1 instanceof LeafRealConst)
                constElement.appendChild(((LeafRealConst)constNode.value1).accept(this));
            if(constNode.value1 instanceof LeafStringConst)
                constElement.appendChild(((LeafStringConst)constNode.value1).accept(this));
            if(constNode.value1 instanceof LeafBool)
                constElement.appendChild(((LeafBool)constNode.value1).accept(this));

            return constElement;
        }

        @Override
        public Element visit(FunNode funNode) {
            Element funElement = doc.createElement(funNode.name);

            //LeafID
            funElement.appendChild(funNode.leaf.accept(this));

            //ArrayList<ParDeclNode>
            if(funNode.pardecl!=null) {
                Element parDeclNodeList = doc.createElement("ParamsList");
                for (ParDeclNode parDeclNode : funNode.pardecl) {
                    parDeclNodeList.appendChild(parDeclNode.accept(this));
                }
            funElement.appendChild(parDeclNodeList);
            }

            //TypeNode
            if(funNode.typeNode != null) {
                Element returnFunType = doc.createElement("ReturnFunType");
                returnFunType.appendChild(funNode.typeNode.accept(this));
                funElement.appendChild(returnFunType);

            }

            //BodyNode
            funElement.appendChild(funNode.bodyNode.accept(this));

            return funElement;

        }

        @Override
        public Element visit(ParDeclNode parDeclNode){
            Element parDeclElement = doc.createElement(parDeclNode.name);


            Element outParam = doc.createElement("ModeOp");
            parDeclElement.appendChild(outParam);
            outParam.appendChild(parDeclNode.mod.accept(this));


            //TypeNode
            parDeclElement.appendChild(parDeclNode.typeNode.accept(this));

            //LeafID
            parDeclElement.appendChild(parDeclNode.leaf.accept(this));



            return parDeclElement;
        }

        public Element visit(ModeParNode node) {
            Element modParNode = doc.createElement(node.mod);

            return modParNode;
        }

        @Override
        public Element visit(StatListNode statListNode){
            Element statListElement = doc.createElement(statListNode.name);

            //ArrayList<StatNode>
            for(StatNode statNode: statListNode.statList) {
                switch (statNode.getClass().getSimpleName()) {
                    case "IfStatNode":
                        IfStatNode ifStat = (IfStatNode) statNode;
                        statListElement.appendChild(ifStat.accept(this));
                        break;
                    case "WhileStatNode":
                        WhileStatNode whileStatNode = (WhileStatNode) statNode;
                        statListElement.appendChild(whileStatNode.accept(this));
                        break;
                    case "ReadStatNode":
                        ReadStatNode readStatNode = (ReadStatNode) statNode;
                        statListElement.appendChild(readStatNode.accept(this));
                        break;
                    case "WriteStatNode":
                        WriteStatNode writeStatNode = (WriteStatNode) statNode;
                        statListElement.appendChild(writeStatNode.accept(this));
                        break;
                    case "AssignStatNode":
                        AssignStatNode assignStatNode = (AssignStatNode) statNode;
                        statListElement.appendChild(assignStatNode.accept(this));
                        break;
                    case "CallFunNode":
                        CallFunNode callFunNode = (CallFunNode) statNode;
                        statListElement.appendChild(callFunNode.accept(this));
                        break;
                    case "ReturnStatNode":
                        ReturnStatNode returnExprNode = (ReturnStatNode) statNode;
                        statListElement.appendChild(returnExprNode.accept(this));
                        break;
                }
            }
            return statListElement;
            }

        @Override
        public Element visit(IfStatNode node){
            Element ifStatElement = doc.createElement(node.name);

            Element conditionNode = doc.createElement("ConditionIf");
            ifStatElement.appendChild(conditionNode);

            //ExprNode
            conditionNode.appendChild(node.expr.accept(this));

            //BodyNode
            ifStatElement.appendChild(node.bn.accept(this));

            //ElseBody
            if(node.elsebody != null) {
                Element elseElement = doc.createElement("ElseBody");
                ifStatElement.appendChild(elseElement);
                elseElement.appendChild(node.elsebody.accept(this));
            }

            return ifStatElement;

            }

        @Override
        public Element visit(WhileStatNode node) {
            Element whileStatElement = doc.createElement(node.name);

            //Expr condition
            Element conditionNode = doc.createElement("ConditionWhile");
            whileStatElement.appendChild(conditionNode);
            conditionNode.appendChild(node.expr.accept(this));

            //BodyNode
            whileStatElement.appendChild(node.bn.accept(this));

            return whileStatElement;
        }

        @Override
        public Element visit(ReadStatNode node) {
            Element readStatElement = doc.createElement(node.name);

            //ArrayList<LeafID>

                Element listElement = doc.createElement("idList");
                readStatElement.appendChild(listElement);
                for(LeafID leafId: node.idlist){
                    listElement.appendChild(leafId.accept(this));
                }

                if(node.expr != null) {
                    readStatElement.appendChild(node.expr.accept(this));
            }
                return readStatElement;
        }

        @Override
        public Element visit(WriteStatNode node) {
            Element writeStatElement = doc.createElement(node.name);

            //Mod
            writeStatElement.appendChild(node.node.accept(this));

            //ExprNode
            writeStatElement.appendChild(node.expr.accept(this));

            return writeStatElement;
        }

        public Element visit(ModeWriteNode node) {
            Element modeWriteNode = doc.createElement(node.name);

            return modeWriteNode;
        }

        @Override
        public Element visit(AssignStatNode node) {
            Element assignStatElement = doc.createElement(node.name);

            //LeafID
            assignStatElement.appendChild(node.leaf.accept(this));

            //ExprNode
            assignStatElement.appendChild(node.expr.accept(this));

            return assignStatElement;
        }

        @Override
        public Element visit(CallFunNode node){
            Element callFunElement = doc.createElement(node.name);

            //LeafID
            callFunElement.appendChild(node.leafID.accept(this));

            //ArrayList<ExprNode>
            if(node.exprList != null) {
                Element exprElement  = doc.createElement("ParamsList");
                callFunElement.appendChild(exprElement);
                for(ExprNode exprNode: node.exprList){
                    exprElement.appendChild(exprNode.accept(this));
                }
            }
            return callFunElement;
        }

        @Override
        public Element visit(ReturnStatNode node){

            Element returnStatElement = doc.createElement(node.name);

            //ExprNode
            returnStatElement.appendChild(node.expr.accept(this));

            return returnStatElement;
        }

        @Override
        public Element visit(ExprNode exprNode) {
            Element node = doc.createElement(exprNode.name);

            if(exprNode.value1 != null && exprNode.value2 != null) {
                node.appendChild(((ExprNode) exprNode.value1).accept(this));
                node.appendChild(((ExprNode) exprNode.value2).accept(this));
            }
            if(exprNode.value1 != null && exprNode.value2 == null) {
                if(exprNode.value1 instanceof LeafIntegerConst)
                    node.appendChild(((LeafIntegerConst)exprNode.value1).accept(this));
                if(exprNode.value1 instanceof LeafRealConst)
                    node.appendChild(((LeafRealConst)exprNode.value1).accept(this));
                if(exprNode.value1 instanceof LeafStringConst)
                    node.appendChild(((LeafStringConst)exprNode.value1).accept(this));
                if(exprNode.value1 instanceof LeafID)
                    node.appendChild(((LeafID)exprNode.value1).accept(this));
                if(exprNode.value1 instanceof LeafBool)
                    node.appendChild(((LeafBool)exprNode.value1).accept(this));
                if (exprNode.value1 instanceof CallFunNode)
                    node.appendChild(((CallFunNode)exprNode.value1).accept(this));
                if(exprNode.value1 instanceof ExprNode)
                    node.appendChild(((ExprNode)exprNode.value1).accept(this));
            }

            if(exprNode.mod != null) {
                Element outParExprNode = doc.createElement(exprNode.name);
                node.appendChild(outParExprNode);
                node.appendChild(exprNode.leaf.accept(this));
            }

            return node;
        }

        @Override
        public Element visit(LeafBool leaf) {
            Element leafElement = doc.createElement(leaf.name);
            leafElement.setAttribute("value",String.valueOf(leaf.value));

            return leafElement;
        }

        @Override
        public Element visit(LeafID leafIdNode) {
            Element leafElement = doc.createElement(leafIdNode.name);
            leafElement.setAttribute("value",leafIdNode.value);

            return leafElement;
        }

        @Override
        public Element visit(LeafIntegerConst leaf) {
            Element leafElement = doc.createElement(leaf.name);
            leafElement.setAttribute("value",leaf.value.toString());

            return leafElement;
        }

        @Override
        public Element visit(LeafRealConst leaf) {
            Element leafElement = doc.createElement(leaf.name);
            leafElement.setAttribute("value",leaf.value.toString());

            return leafElement;
        }

        @Override
        public Element visit(LeafStringConst leaf) {
            Element leafElement = doc.createElement(leaf.name);
            leafElement.setAttribute("value", leaf.value);

            return leafElement;
        }


    public void saveXML(String name) throws TransformerException {
        // write the content into xml file
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        DOMSource source = new DOMSource(doc);

        //StreamResult result = new StreamResult(new File(name.substring(0, name.length()-4).split("/")[1]+ ".xml"));
        StreamResult result = new StreamResult(new File("test_files" + File.separator + "syntax_tree" + File.separator + name.substring(0, name.length()-4) + ".xml"));
        transformer.transform(source, result);
    }


}
