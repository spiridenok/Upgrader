package wizard;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

import asml.dsl.ddf.DdfStandaloneSetup;
import asml.dsl.ddf.ddf.ArrayDefinition;
import asml.dsl.ddf.ddf.Attribute;
import asml.dsl.ddf.ddf.AttributeKind;
import asml.dsl.ddf.ddf.Attributes;
import asml.dsl.ddf.ddf.BasicTypeReference;
import asml.dsl.ddf.ddf.ConstantValue;
import asml.dsl.ddf.ddf.ConstantValueReference;
import asml.dsl.ddf.ddf.DDFP_CONSTANT;
import asml.dsl.ddf.ddf.DDFType;
import asml.dsl.ddf.ddf.Definition;
import asml.dsl.ddf.ddf.Dimension;
import asml.dsl.ddf.ddf.Model;
import asml.dsl.ddf.ddf.MultipleStringsExpression;
import asml.dsl.ddf.ddf.SetCommand;
import asml.dsl.ddf.ddf.SetCommandBlock;
import asml.dsl.ddf.ddf.StructDefinition;
import asml.dsl.ddf.ddf.StructField;
import asml.dsl.ddf.ddf.TypeDefinition;
import asml.dsl.ddf.ddf.TypeDefinitionWithAttributes;
import asml.dsl.ddf.ddf.TypeReference;
import asml.dsl.ddf.ddf.TypeSelector;
import asml.dsl.ddf.ddf.TypeSelectorIdentifier;
import asml.dsl.ddf.ddf.VariableDimension;

public class ManualDiff 
{

	private Resource orgResource;
	private ResourceSet rs;
	private ResourceSet newRs;
	private Model orgModel;
	private Model newModel;
	private ChangeModel change_model;
	private String org_dir;
	private String new_dir;

	private String get_ddf_file_name( final String dir, final String ddf_type )
	{		
		return "file:///" + dir.replace("\\", "/") + "/" + ddf_type.substring(0, ddf_type.indexOf(':')) + ".ddf";
	}
	
	public ManualDiff( ChangeModel model, String org_dir, String new_dir, String mcs_type ) 
	{
		 change_model = model;
		 this.org_dir = org_dir;
		 this.new_dir = new_dir;
		
		 DdfStandaloneSetup.doSetup();

		 rs = new ResourceSetImpl();
		 newRs = new ResourceSetImpl();
		
		orgResource = rs.getResource(URI.createURI( get_ddf_file_name(org_dir, mcs_type ) ), true);
		Resource newResource = newRs.getResource( URI.createURI( get_ddf_file_name(new_dir, mcs_type ) ), true);

		orgModel = (Model) orgResource.getContents().get(0);
		newModel = (Model) newResource.getContents().get(0);
		
		TypeDefinitionWithAttributes old_mcs = find_mcs_object(orgModel, mcs_type);

		/* MC is always a structure */
		StructDefinition org_sd = (StructDefinition) old_mcs.getTypeDefinition();

		check_types(org_sd, newModel, "top_level");

//		System.out.println("FINISHED");
	}

	private void check_types(StructDefinition org_sd, Model new_model, String qualified_name) 
	{
		TypeDefinitionWithAttributes new_mcs = resolve_type(newRs, org_sd.getName());

		if (new_mcs == null) 
		{
			// System.out.println( "Null for: " + org_sd.getName() + ":" + sf.getField() + ":" + sf.getType().getClass());
			// TypeReference tr = (TypeReference) sf.getType();

			// System.out.println( "Type definition: " + tr.getType().getName() );
			String type_name = org_sd.getName();
			String name = get_ddf_file_name(new_dir, type_name);

			try 
			{
				newRs.getResource(URI.createURI(name), true);
			} 
			catch (Exception e) 
			{
				System.out.println("??? Can not resolve file " + name + " for field " + type_name);
				change_model.add_unresolved_ddf(name.replaceAll("file:///", ""));
				return;
			}
		}
		
		new_mcs = resolve_type(newRs, org_sd.getName());
		if( new_mcs == null )
		{
			System.out.println( "In new can not find the type " + org_sd.getName() );
			return;
		}
		
		StructDefinition new_sd = (StructDefinition) new_mcs.getTypeDefinition();
		if( !(new_sd instanceof StructDefinition) )
		{
			System.out.println( "Type change from struct " + org_sd.getName() + " to " + new_mcs.getTypeDefinition().getName());
		}
		
		// System.out.println( "--------------- " + org_sd.getName() + "-------------------------------" );
		for (StructField sf : org_sd.getStructFieldList()) 
		{
			// System.out.println("field: " + sf.getField() + ", type: " + sf.getType());
			// System.out.println("field: " + sf.getField() + ", attrs: " + sf.getAttibutes());
			final String full_field_name = qualified_name + "." + sf.getField();

			StructField new_sf = find_struct_field(new_sd, sf.getField());
			if (new_sf == null) 
			{
				System.out.println("Field is not found: " + full_field_name);
				return;
			}
			
			List<Attribute> org_attrs = get_attr_defs(AttributeKind.DEFAULT_ATT, sf, org_sd, rs);
			List<Attribute> new_attrs = get_attr_defs(AttributeKind.DEFAULT_ATT, new_sf, new_sd, newRs);
			
			if ( find_default_attr_diff(org_attrs, new_attrs) )
			{
				System.out.println("Attributes changed for " + full_field_name);
				DDFType type = sf.getType();
				boolean var_array = false;
				if( type instanceof TypeReference )
				{
					TypeReference tr = (TypeReference)type;
					if( tr.getType() instanceof ArrayDefinition )
					{
						ArrayDefinition ad = (ArrayDefinition)tr.getType();
						for( Dimension d : ad.getDimensions() )
							if( d instanceof VariableDimension )
								var_array = true;
					}
				}
				if( var_array )
					change_model.add_changed_var_array_len(full_field_name);
				else
					change_model.add_changed_default( full_field_name );
			}
			
			org_attrs = get_attr_defs(AttributeKind.TO_DEFAULT_ATT, sf, org_sd, rs);
			new_attrs = get_attr_defs(AttributeKind.TO_DEFAULT_ATT, new_sf, new_sd, newRs);
			
			if (find_const_type_change(sf.getAttibutes(), new_sf.getAttibutes()))
			{
				System.out.println("Const type changed for " + full_field_name);
				change_model.add_changed_const_type(full_field_name);
			}

			boolean types_are_ok = same_types(sf.getType(), new_sf.getType());
			// System.out.println(" types are equal: " + types_are_ok );
			if (!types_are_ok) 
			{
				System.out.println("Different types for field '" + full_field_name + "' " + get_type(sf.getType()) + " -> " + get_type(new_sf.getType()));
				change_model.add_changed_data_type(full_field_name);
			}

			if (sf.getType() instanceof TypeReference) 
			{
				TypeReference tr = (TypeReference) sf.getType();
				// if( tr.getType().getName().startsWith("DNDMxDEF:"))
				// System.out.println( "Need to resolve + " + tr.getType().getName() );
				// System.out.println( "Type is " + tr.getClass() + ":" + tr.getType());
				if (tr.getType() instanceof StructDefinition) 
				{
					check_types((StructDefinition) tr.getType(), new_model, full_field_name);
				} 
				else if (tr.getType() instanceof TypeDefinition) 
				{
					
					// System.out.println( "Type definition: " + tr.getType().getName() );
					String name = get_ddf_file_name(org_dir, tr.getType().getName());

					if (resolve_type(rs, tr.getType().getName()) == null) 
					{
						try 
						{
							rs.getResource(URI.createURI(name), true);
						}
						catch (Exception e) 
						{
							System.out.println("!!! Can not resolve file " + name + " for field " + sf.getField());
							change_model.add_unresolved_ddf(name.replaceAll("file:///", ""));
						}
					}
					
					TypeDefinitionWithAttributes new_type = resolve_type(rs, tr.getType().getName());
					if( new_type == null )
					{
						System.out.println( "Can not find the type: " + tr.getType().getName() );
					}
					else if( new_type.getTypeDefinition() instanceof StructDefinition )
					{
						check_types(((StructDefinition)new_type.getTypeDefinition()), orgModel, full_field_name);
					}
				}
			}
		}
	}

	private String get_type(DDFType ddfType) 
	{
		if (ddfType instanceof BasicTypeReference)
			return ((BasicTypeReference) ddfType).getType().getName();
		if (ddfType instanceof TypeReference)
			return ((TypeReference) ddfType).getType().getName();
		else
			return "Unknown type " + ddfType.getClass();
	}

	private boolean same_types(DDFType o, DDFType n) 
	{
		if (o instanceof BasicTypeReference && n instanceof BasicTypeReference) 
		{
			return ((BasicTypeReference) o).getType() == ((BasicTypeReference) n).getType();
		}
		if (o instanceof TypeReference && n instanceof TypeReference) 
		{
			return ((TypeReference) o).getType().getName().equals(((TypeReference) n).getType().getName());
		}
		return false;
	}

	private StructField find_struct_field(StructDefinition def, String sf_name) 
	{
		for (StructField new_sf : def.getStructFieldList()) 
		{
			if (new_sf.getField().equals(sf_name)) 
			{
				return new_sf;
			}
		}
		return null;
	}

	private TypeDefinitionWithAttributes find_mcs_object( final Model model, final String mcs_type_name) 
	{
		for (Definition def : model.getDefinitions()) 
		{
			if (def instanceof TypeDefinitionWithAttributes) 
			{
				TypeDefinitionWithAttributes typedef = (TypeDefinitionWithAttributes) def;
				if (typedef.getTypeDefinition().getName().equals(mcs_type_name))
					return typedef;
			}
		}
		return null;
	}

	private TypeDefinitionWithAttributes resolve_type(ResourceSet set, String type_name) 
	{
		for (Resource res : set.getResources()) 
		{
			// If failed to load a resource (for example because the file does not exist),
			// that resource is still in the set, but will have no content
			if( res.getContents().size() > 0 )
			{
				TypeDefinitionWithAttributes td = find_mcs_object((Model) res.getContents().get(0), type_name);
				if (td != null) 
				{
					return td;
				}
			}
		}
		return null;
	}

	private boolean find_default_attr_diff(List<Attribute> org_attrs, List<Attribute> new_attrs) 
	{
		Attribute org_default = get_attr(AttributeKind.DEFAULT_ATT, org_attrs);
		Attribute new_default = get_attr(AttributeKind.DEFAULT_ATT, new_attrs);

		if ((org_default == null && new_default != null) || (org_default != null && new_default == null)) 
		{
			System.out.println("Default is added");
			return true;
		} 
		else if (org_default != null && new_default != null) 
		{
			if (org_default.getValue() instanceof DDFP_CONSTANT) 
			{
				DDFP_CONSTANT org_const = (DDFP_CONSTANT) org_default.getValue();

				if (new_default.getValue() instanceof DDFP_CONSTANT) 
				{
					DDFP_CONSTANT new_const = (DDFP_CONSTANT) new_default.getValue();

					if (!org_const.getStringValue().equals(new_const.getStringValue())) 
					{
						System.out.println("Default value changed from "
								+ org_const.getStringValue() + " to "
								+ new_const.getStringValue());
						return true;
					}
				}
			}
			if (org_default.getValue() instanceof ConstantValueReference) 
			{
				ConstantValue org_const = ((ConstantValueReference) org_default.getValue()).getRef();

				if (new_default.getValue() instanceof ConstantValueReference) 
				{
					ConstantValue new_const = ((ConstantValueReference) new_default.getValue()).getRef();

					// TODO: this is not complete! It's not enough to check if
					// the names are different,
					// it must also be checked if they refer to the same value.
					// Example: VAL_A != VAL_B but no changes in default is
					// detected if
					// VAL_A = 10 and VAL_B = 10.
					if (!org_const.getName().equals(new_const.getName())) 
					{
						System.out.println("Default value changed from "
								+ org_const.getName() + " to "
								+ new_const.getName());
						return true;
					}
				}
			}
		}
		return false;
	}

	private Attribute get_attr(AttributeKind kind, List<Attribute> attrs) {
		for (Attribute attr : attrs)
			if (attr.getAttributeKind() == kind)
				return attr;
		return null;
	}

	private boolean find_const_type_change(Attributes org_attrs,Attributes new_attrs) 
	{
		Attribute org_to_default = get_attr(AttributeKind.TO_DEFAULT_ATT, org_attrs);
		Attribute new_to_default = get_attr(AttributeKind.TO_DEFAULT_ATT, new_attrs);

		// DDF reports strings including quotes
		String org_string = "\"on_creation\"";
		String new_string = "\"on_creation\"";

		if (org_to_default != null)
			if (org_to_default.getValue() instanceof MultipleStringsExpression)
				org_string = ((MultipleStringsExpression) org_to_default.getValue()).getStrings().get(0);
		if (new_to_default != null)
			if (new_to_default.getValue() instanceof MultipleStringsExpression)
				new_string = ((MultipleStringsExpression) new_to_default.getValue()).getStrings().get(0);

		if (org_string.equals(new_string)) 
		{
			return false;
		} 
		else 
		{
			System.out.println(org_string + " <-> " + new_string);
			return true;
		}
	}

	private Attribute get_attr(AttributeKind kind, Attributes attrs) 
	{
		if (attrs == null)
			return null;
		for (Attribute attr : attrs.getAttributeList())
			if (attr.getAttributeKind() == kind)
				return attr;
		return null;
	}

	private Attributes get_set_attributes(ResourceSet resource_set, String struct_name, String field_name) 
	{
		TypeDefinitionWithAttributes sd_def = resolve_type(resource_set, struct_name);

		for (SetCommandBlock command : sd_def.getSetCommandBlocks()) 
		{
			for (SetCommand set_command : command.getSetCommandList()) 
			{
				if (set_command.getSetname() instanceof TypeSelector) 
				{
					TypeSelector selector = (TypeSelector) set_command.getSetname();
					if (selector.getHeadElement() instanceof TypeSelectorIdentifier) 
					{
						TypeSelectorIdentifier selector_id = (TypeSelectorIdentifier) selector.getHeadElement();
						if (selector_id.getTid().equals(field_name)) 
						{
							return set_command.getAttributes();
						}
					}
				}
			}
		}
		return null;
	}
	
	private List<Attribute> get_attr_defs( AttributeKind kind, StructField sf, StructDefinition org_sd, ResourceSet rs )
	{
		// First search "set" attributes for the right attribute kind 
		Attributes org_set_attrs = get_set_attributes(rs, org_sd.getName(), sf.getField());
		if( org_set_attrs != null )
			if( get_attr(kind, org_set_attrs ) != null )
				return org_set_attrs.getAttributeList();

		// If this attribute kind is not found in 'set' - search the struct definition
		if( sf.getAttibutes() != null )
			if( get_attr(kind, sf.getAttibutes().getAttributeList()) != null )
				return sf.getAttibutes().getAttributeList();			
		
		// Not found in the struct definition - search in the data type definition
		TypeDefinitionWithAttributes org_type = resolve_type( rs, get_type( sf.getType() ) );
		
		if ( org_type == null && sf.getType() instanceof TypeDefinition ) 
		{
			String name = get_ddf_file_name(org_dir, get_type( sf.getType() ));
			try 
			{
				rs.getResource(URI.createURI(name), true);
			}
			catch (Exception e) 
			{
				System.out.println("!!! Can not resolve file " + name + " for field " + sf.getField());
				change_model.add_unresolved_ddf(name.replaceAll("file:///", ""));
			}
		}			
		org_type = resolve_type( rs, get_type( sf.getType() ) );
		if( org_type != null )
		{
			Attributes org_type_attrs = org_type.getMainAttributes();
			
			if( org_type_attrs != null )
				if( get_attr(kind, org_type_attrs.getAttributeList()) != null )
					return org_type_attrs.getAttributeList();
		}
		
		// Not find at all - no value is specified for this attribute kind
		return new ArrayList<Attribute>();
	}
}
