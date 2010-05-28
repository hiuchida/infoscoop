package org.infoscoop.dao;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.infoscoop.dao.model.Gadget;
import org.infoscoop.util.SpringUtil;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class GadgetDAO extends HibernateDaoSupport {
	public static GadgetDAO newInstance(){
		
        return (GadgetDAO)SpringUtil.getContext().getBean("gadgetDAO");
    }
	
	public static void main(String args[]) throws IOException{
	}
	
	public Gadget select( String type ) {
		return select( type,"/",type +".xml");
	}
	public List<Gadget> selectGadgetXMLs() {
		String queryString = "from Gadget where path = '/' and name in "
			+"( select concat(type,'.xml') from Gadget ) and name = concat(type,'.xml')";
		
		return super.getHibernateTemplate().find( queryString );
	}
	public Gadget select(String type, String path,String name ) {
		//select data from ${schema}.gadget where type = ? and fileType = ?
		List result = ( List )super.getHibernateTemplate().findByCriteria(
				DetachedCriteria.forClass( Gadget.class )
					.add( Restrictions.eq( "type",type ))
					.add( Restrictions.eq( "path",path ))
					.add( Restrictions.eq( "name",name )));
		if( result == null || result.size() == 0 )
			return null;
		
		return ( Gadget )result.get(0);
	}
	
	public void insert(String type,String path,String name, byte[] xml) {
		_insert( type,path,name,xml );
	}
	public void _insert(String type,String path,String name, byte[] xml) {
		Gadget gadget = new Gadget();
		gadget.setType( type );
		gadget.setPath( path );
		gadget.setName( name );
		gadget.setData( xml );
		gadget.setLastmodified( new Date());
		
		super.getHibernateTemplate().save( gadget );
	}
	
	public void update( String type,String path,String name,byte[] data ) {
		Gadget resource = select( type,path,name );
		
		if( resource == null )
			return;
		
		resource.setData( data );
		resource.setLastmodified( new Date() );
		
		super.getHibernateTemplate().update( resource );
	}
	
	public boolean delete( String type,String path,String name ) {
		Gadget resource = select( type,path,name );
		
		System.out.println( type+","+path+","+name+"="+resource );
		if( resource == null )
			return false;
		
		super.getHibernateTemplate().delete( resource );
		return true;
	}
	
	public int deleteType(String type) {
		//delete from ${schema}.gadget where type = ?
		String queryString = "delete from Gadget where Type = ?";
		
		return super.getHibernateTemplate().bulkUpdate( queryString,
				new Object[] { type } );
	}
	
	public List<Gadget> list( String type ) {
		return super.getHibernateTemplate().findByCriteria( DetachedCriteria.forClass( Gadget.class )
				.add( Restrictions.eq( "type",type ))
				.addOrder( Order.asc( "name" )));
	}
	
	public List<Gadget> list( String type,String path ) {
		return super.getHibernateTemplate().findByCriteria( DetachedCriteria.forClass( Gadget.class )
				.add( Restrictions.eq( "type",type ))
				.add( Restrictions.eq( "path",path ))
				.addOrder( Order.asc( "name" )));
	}
}