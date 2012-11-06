gen2ools
========

service generation tools for Eucalyptus

1. build gen2ools
   $ ant

2. Extract the schema from your wsdl and replace unsupported XSD types with xsd:string, e.g.:
   $ xpath -e /definitions/types/schema vim.wsdl | sed 's/xsd:any/xsd:stringType/g' > vim-types.xsd

3. Run the binding compiler
   $ java -jar gen2ools.jar -d bindings -n com.eucalyptus.vmware vim-types.xsd 

4. Find the generated binding and classes in 'bindings'  
