import {hoverTooltip} from "@codemirror/tooltip";
import {syntaxTree} from "@codemirror/language";
import {Sexpr} from "./spec";
import {SyntaxNode} from "lezer-tree";
import { EditorState } from "@codemirror/state";

type SpecResolver = (form : Sexpr) => Sexpr | null;
type SpecArgResolver = (form : Sexpr, argPos : number) => Sexpr | null;

function findNamespace(state : EditorState, startAt : SyntaxNode) : string | null {
    let cursor = startAt.cursor;
    cursor.parent();
    cursor.parent();
    if(cursor.type.name == "NamespacedMap"){
        cursor.next(); // KeywordPrefix
        let {from, to} = cursor;
        return state.doc.sliceString(from+1, to);
    }
    else return null;
}

function extractKeyword(state: EditorState, node : SyntaxNode) : string {
    let {from, to} = node;
    let kw = state.doc.sliceString(from, to);
    let parent = findNamespace(state, node);
    if (kw.includes("/")){
        return kw;
    } else if (parent != null){
        return parent + "/" + kw.substring(1); // drop ":"
    }
    else return kw;
}

function extractFnSpec(resolveArg : SpecArgResolver, state: EditorState, node: SyntaxNode) : string | null {
    let cursor = node.cursor;
    cursor.parent();
    console.log(cursor.type.name);
    if(cursor.type.name == "List"){
        let count = 0;
        cursor.next();
        console.log(cursor.type.name);
        cursor.next();
        console.log(cursor.type.name);
        let sym = state.doc.sliceString(cursor.from, cursor.to);
        while(cursor.from < node.from){
            count++;
            cursor.nextSibling();
            console.log(cursor.type.name);
        }
        return resolveArg(sym, count);
    }
    else return null;
}

export function specTooltip(resolve : SpecResolver, resolveArg : SpecArgResolver){
    return hoverTooltip((view, pos, side) => {
        let node = syntaxTree(view.state).resolve(pos);
        let {name, from, to} = node;
        if (from == pos && side < 0 || to == pos && side > 0){
            return null;
        } else{
            let text : string;
            if(name == "Keyword"){
                text = extractKeyword(view.state, node);
            } else {
                text = view.state.doc.sliceString(from, to);
            }

            let fnSpec = extractFnSpec(resolveArg, view.state, node);
            let spec = resolve(text) || "No spec found";
            spec = [fnSpec, resolve(text)].filter(x => !!x).join("\n");
            return {
                pos: from,
                end: to,
                above: true,
                create(_view) {
                    let dom = document.createElement("pre");
                    dom.textContent = spec;
                    dom.classList.add("hf-code-tooltip");
                    return {dom};
                }
            }
        };
    });
}
