package test;

import java.util.List;

import javax.jws.WebParam.Mode;
import javax.lang.model.type.ReferenceType;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

import asml.dsl.ddf.DdfStandaloneSetup;
import asml.dsl.ddf.ddf.BasicTypeReference;
import asml.dsl.ddf.ddf.DDFType;
import asml.dsl.ddf.ddf.Definition;
import asml.dsl.ddf.ddf.Model;
import asml.dsl.ddf.ddf.StructDefinition;
import asml.dsl.ddf.ddf.StructField;
import asml.dsl.ddf.ddf.TypeDefinition;
import asml.dsl.ddf.ddf.TypeDefinitionWithAttributes;
import asml.dsl.ddf.ddf.TypeReference;
import asml.dsl.ddf.query.DdfEObjectRetriever;
import asml.dsl.ddf.services.DdfGrammarAccess.TypeReferenceElements;

public class ManualDiff {

	private static Resource orgResource;
	private static ResourceSet rs;
	private static ResourceSet newRs;
	
	public static void main(String[] args) {
		DdfStandaloneSetup.doSetup();
		
		rs = new ResourceSetImpl();
//		Resource orgDefResource = rs.getResource(URI.createURI("DNDMxDEF.ddf"), true);
		orgResource = rs.getResource(URI.createURI("DNDMxTRxDEF.ddf"), true);
		newRs = new ResourceSetImpl();
//		Resource newDefResource = newRs.getResource(URI.createURI("DNDMxDEF.ddf"), true);
		Resource newResource = newRs.getResource(URI.createURI("DNDMxTRxDEF_new.ddf"), true);
//		Resource resource = rs.getResource(URI.createFileURI("h:/garbage/DNDMxTRxDEF.ddf"), true);
		EObject eObject = orgResource.getContents().get(0);
		Model orgModel = (Model)eObject;
		Model newModel = (Model)newResource.getContents().get(0);
		
		final String mcs_type = "DNDMxTRxDEF:trace_info_struct";
		
		TypeDefinitionWithAttributes old_mcs = find_mcs_object(orgModel, mcs_type);		
		TypeDefinitionWithAttributes new_mcs = find_mcs_object(newModel, mcs_type);

		/* MC is always a structure*/
		StructDefinition org_sd = (StructDefinition)old_mcs.getTypeDefinition();
		StructDefinition new_sd = (StructDefinition)new_mcs.getTypeDefinition();

		check_types(org_sd, newModel, "top_level" );
	}
	
	private static void check_types( StructDefinition org_sd, Model new_model, String qualified_name )
	{
		System.out.println( "--------------- " + org_sd.getName() + "-------------------------------" );
		for( StructField sf : org_sd.getStructFieldList() )
		{
			System.out.println("field: " + sf.getField() + ", type: " + sf.getType());
			final String full_field_name = qualified_name + "." + sf.getField();
			
//			TypeDefinitionWithAttributes new_mcs = find_mcs_object( new_model, org_sd.getName() );
			TypeDefinitionWithAttributes new_mcs = resolve_type( newRs, org_sd.getName());

			if( new_mcs == null )
			{
				System.out.println( "Null for: " + org_sd.getName()  + ":" + sf.getField() + ":" + sf.getType().getClass());
//				TypeReference tr = (TypeReference) sf.getType();
				
//				System.out.println( "Type definition: " + tr.getType().getName() );
				String type_name = org_sd.getName();
				
				try
				{
					// TODO: just a hack to load "_new.dff". needs to be changed before going into production!
					String name = type_name.substring(0,type_name.indexOf(':'))+"_new.ddf";
					newRs.getResource(URI.createURI(name), true);
					System.out.println(name + " is loaded for NEW!" );
					new_mcs = resolve_type( newRs, org_sd.getName());
				}
				catch(Exception e)
				{
					System.out.println("!!! Can not resolve file " + type_name + " for field " + sf.getField());
				}
			}
			StructDefinition new_sd = (StructDefinition)new_mcs.getTypeDefinition();
//			System.out.println( "new sd: " + new_sd );
			
			StructField new_sf = find_struct_field(new_sd, sf.getField() );
			if ( new_sf == null )
			{
				System.out.println("Field is not found: " + full_field_name );
				return;
			}
			
			boolean types_are_ok = same_types( sf.getType(), new_sf.getType() );
//			System.out.println(" types are equal: " + types_are_ok  );
			if( !types_are_ok )
			{
				System.out.println("Different types for field '" + full_field_name + "' " + get_type( sf.getType() ) + " -> " + get_type( new_sf.getType() ) );
			}
			
			if( sf.getType() instanceof TypeReference )
			{
				TypeReference tr = (TypeReference) sf.getType();
//				if( tr.getType().getName().startsWith("DNDMxDEF:"))
//					System.out.println( "Need to resolve + " + tr.getType().getName() );
//				System.out.println( "Type is " + tr.getClass() + ":" + tr.getType());
				if( tr.getType() instanceof StructDefinition)
				{
					check_types((StructDefinition)tr.getType(), new_model, full_field_name );
				}
				else if( tr.getType() instanceof TypeDefinition )
				{
					System.out.println( "Type definition: " + tr.getType().getName() );
					String name = tr.getType().getName().substring(0,tr.getType().getName().indexOf(':'))+".ddf";
					
					try
					{
						if( resolve_type(rs, tr.getType().getName() ) != null )
						{
							System.out.println( "Existing type " + tr.getType() );
						}
						else
						{
							rs.getResource(URI.createURI(name), true);
							System.out.println(name + " is loaded!" );
						}
					}
					catch(Exception e)
					{
						System.out.println("!!! Can not resolve file " + name + " for field " + sf.getField());
					}
				}
			}
		}
	}
	
	private static String get_type( DDFType ddfType )
	{
		if( ddfType instanceof BasicTypeReference )
			return ((BasicTypeReference)ddfType).getType().getName();
		if( ddfType instanceof TypeReference )
			return ((TypeReference)ddfType).getType().getName();
		else
			return "Unknown type " + ddfType.getClass();
	}
		
	private static boolean same_types( DDFType o, DDFType n ) 
	{
		if( o instanceof BasicTypeReference && n instanceof BasicTypeReference )
		{
			return ((BasicTypeReference)o).getType() == ((BasicTypeReference)n).getType();
		}
		if( o instanceof TypeReference && n instanceof TypeReference )
		{
			return ((TypeReference)o).getType().getName().equals( ((TypeReference)n).getType().getName() );
		}
		return false;
	}

	private static StructField find_struct_field( StructDefinition def, String sf_name )
	{
		for( StructField new_sf: def.getStructFieldList() )
		{
			if( new_sf.getField().equals( sf_name ) )
			{
				return new_sf;
			}
		}
		return null;
	}
	
	private static TypeDefinitionWithAttributes find_mcs_object( final Model model, final String mcs_type_name )
	{
		for( Definition def: model.getDefinitions() )
		{
			if (def instanceof TypeDefinitionWithAttributes )
			{
				TypeDefinitionWithAttributes typedef = (TypeDefinitionWithAttributes)def;
				if( typedef.getTypeDefinition().getName().equals( mcs_type_name ) )
					return typedef;
			}
		}
		return null;
	}

	private static TypeDefinitionWithAttributes resolve_type( ResourceSet set, String type_name )
	{
		for( Resource res:  set.getResources() )
		{
			TypeDefinitionWithAttributes td = find_mcs_object( (Model)res.getContents().get(0), type_name );
			if( td != null )
			{
				System.out.println( "Found type: " + type_name );
				return td;
			}			
		}
		return null;
	}
}
