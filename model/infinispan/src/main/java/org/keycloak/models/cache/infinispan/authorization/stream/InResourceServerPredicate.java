package org.keycloak.models.cache.infinispan.authorization.stream;

import org.keycloak.models.cache.infinispan.authorization.entities.InResourceServer;
import org.keycloak.models.cache.infinispan.entities.Revisioned;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.util.Map;
import java.util.function.Predicate;
import org.infinispan.commons.marshall.Externalizer;
import org.infinispan.commons.marshall.MarshallUtil;
import org.infinispan.commons.marshall.SerializeWith;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@SerializeWith(InResourceServerPredicate.ExternalizerImpl.class)
public class InResourceServerPredicate implements Predicate<Map.Entry<String, Revisioned>>, Serializable {
    private String serverId;

    public static InResourceServerPredicate create() {
        return new InResourceServerPredicate();
    }

    public InResourceServerPredicate resourceServer(String id) {
        serverId = id;
        return this;
    }

    @Override
    public boolean test(Map.Entry<String, Revisioned> entry) {
        Object value = entry.getValue();
        if (value == null) return false;
        if (!(value instanceof InResourceServer)) return false;

        return serverId.equals(((InResourceServer)value).getResourceServerId());
    }

    public static class ExternalizerImpl implements Externalizer<InResourceServerPredicate> {

        private static final int VERSION_1 = 1;

        @Override
        public void writeObject(ObjectOutput output, InResourceServerPredicate obj) throws IOException {
            output.writeByte(VERSION_1);

            MarshallUtil.marshallString(obj.serverId, output);
        }

        @Override
        public InResourceServerPredicate readObject(ObjectInput input) throws IOException, ClassNotFoundException {
            switch (input.readByte()) {
                case VERSION_1:
                    return readObjectVersion1(input);
                default:
                    throw new IOException("Unknown version");
            }
        }

        public InResourceServerPredicate readObjectVersion1(ObjectInput input) throws IOException, ClassNotFoundException {
            InResourceServerPredicate res = new InResourceServerPredicate();
            res.serverId = MarshallUtil.unmarshallString(input);

            return res;
        }
    }
}
