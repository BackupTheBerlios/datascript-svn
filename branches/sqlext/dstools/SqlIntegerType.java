package datascript;

import java.util.Iterator;

import datascript.Field.LookAhead;

//It will be only used for sql3Emitter.java
public class SqlIntegerType extends CompoundType {

	SqlIntegerType(String name, CompoundType parent) {
	      super(name, parent);
	   }

	   public IntegerValue sizeof(Context ctxt) {
	      IntegerValue size = new IntegerValue(0);
	      IntegerValue eight = new IntegerValue(8);

	      for (int i = 0; i < fields.size(); i++) {
		 Field fi = (Field)fields.elementAt(i);
		 // get the element of Vector
		 try {
		    BuiltinType b = BuiltinType.getBuiltinType(fi.getType());
		    if (b instanceof BitFieldType) {
		       size = size.add(new IntegerValue(((BitFieldType)b).getLength()));
		       continue;
		    } 
		 } catch (ClassCastException _) {
		 }
		 size = size.add(fi.sizeof(ctxt).multiply(eight));
	      }
	      return size.divide(eight);
	      // why do that?,  (num *8) / 8 = num (?!)
	   }

	   public boolean isMember(Context ctxt, Value val) {
	      // do something like
	      // if val.getType() == this
	      throw new ComputeError("isMember not implemented");
	   }

	   void computeBitFieldOffsets(int offset) {
	      if (bfoComputed) {
		 return;
	      }
	      bfoComputed = true;
	      Main.debug("computing bfo for " + this + " offset=" + offset);

	      Field startField = null;
	      for (Iterator i = getFields(); i.hasNext(); ) {
		 Field f = (Field)i.next();
		 TypeInterface ftype = f.getType();

		 if (f.isBitField()) {
		    BitFieldType bftype;
		    bftype = (BitFieldType)BuiltinType.getBuiltinType(ftype);
		    if (startField == null) {
		       startField = f;
		    }
		    f.bitFieldStart = startField;
		    f.bitFieldOffset = offset;
		    offset += bftype.length;
		 } else {
		    /* handle anonymous compound types that are embedded at
		     * partial bit offsets
		     */
		    if (offset % 8 != 0 && ftype instanceof CompoundType) {
		       CompoundType ctype = (CompoundType)ftype;
		       if (ctype.isAnonymous()) {
			  ctype.computeBitFieldOffsets(offset);
		       }
		    }

		    if (startField != null) {
		       /*
		       if (offset % 8 != 0) {
			  throw new InternalError("bitfields that are not integer " +
				"multiples of bytes are not supported, please pad");
		       }
		       */
		       startField.totalBitFieldLength = offset;
		       startField = null;
		       offset = 0;
		    }
		 }
	      }
	      if (startField != null) {
		 startField.totalBitFieldLength = offset;
		 /*
		 if (offset % 8 != 0) {
		    throw new InternalError("bitfields that are not integer " +
		       "multiples of bytes are not supported, please pad");
		 }
		 */
	      }
	   }

	    public Field.LookAhead getLookAhead() {
		return getField(0).getLookAhead();
	    }
}
