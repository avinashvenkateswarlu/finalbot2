function toWords(a)
{  
    return eval(a); 
}

toWords(eval("\""+params.get("input")+"\""))