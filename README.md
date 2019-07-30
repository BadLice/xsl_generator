xml dei dati (presi dal logger): dati_def.xml
xml della struttura del template: template.xml
xsl generato: PDF_riass.xsl

struttura del file template.xml:
<?xml version="1.0" encoding="ISO-8859-1"?> <!-- Codifica del file (NON CAMBIARE l'attributo encoding) -->
<_> <!-- elemento di root OBBLIGATORIO -->
	<!-- Oggetti del template -->
</_>

tipi di oggetti del template:
1. Module = modulo del template
Attributi: 
	- label: Commento del modulo (ad esempio "MODULO 1 DI 4") 
	- name: Nome del modulo

2. Fieldset = fieldset del template
Attributi:
	- label: Nome del fieldset
	- if: condizione if da associare al fieldset (scritta in linguaggio xsl)

3. Submodule = sottomodulo del template (FUNZIONANO FINO AL LIVELLO 1, OVVERO NON FUNZIONANO I SOTTO-SOTTOMODULI E COSì VIA)
Attributi:
	- name: nome del sottomodulo a template (nome inteso come nome della variabile, esempio: "Altri_Doc")
	- label: nome del sottomodulo da mostrare sul pdf (esempio: "Altri documenti")
	- required-item: nome di un Item obbligatorio del sottomodulo (esemptio: "Doc_Descr")

4. Item = item del sottomodulo
ATTENZIONE! i tag degli item vanno chiamati con il nome dell'item stesso
Esempio:
- nome item a template: Richiedente_CodFisc
- etichetta item a template: Codice fiscale del richiedente
- struttura sul file template.xml:
	<Richiedente_CodFisc>Codice fiscale del richiedente</Richiedente_CodFisc>
Attributi:
	- if: condizione if da associare all'item (scritta in linguaggio xsl)
	- integer: da valorizzare a true (o a qualsiasi altro valore, basta metterlo) se il campo è di tipo numero intero
	- decoded: da valorizzare a true (o a qualsiasi altro valore, basta metterlo) se il campo è di tipo radio o checkbox o menu (prende il decoded)
	- euro: da valorizzare a true (o a qualsiasi altro valore, basta metterlo) se il campo è di tipo valuta euro

LIBRERIE NECESSARIE:
Apache FOP installato a sistema e inserito nella path di sistema.

PER GENERARE UN DOCUMENTO LANCIARE IL COMANDO:
java -cp "path\to\XSL_Auto" Main && fop -xml dati_def.xml -xsl PDF_riass.xsl -pdf output.pdf && output.pdf