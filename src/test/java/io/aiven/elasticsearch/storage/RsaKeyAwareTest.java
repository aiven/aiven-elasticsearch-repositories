/*
 * Copyright 2020 Aiven Oy
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.aiven.elasticsearch.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.EncodedKeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemWriter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.io.TempDir;

public abstract class RsaKeyAwareTest {

    public static KeyPair rsaKeyPair;

    public static Path publicKeyPem;

    public static Path privateKeyPem;

    @BeforeAll
    static void generateRsaKeyPair(@TempDir final Path tmpFolder) throws NoSuchAlgorithmException, IOException {
        final var keyPair = KeyPairGenerator.getInstance("RSA", new BouncyCastleProvider());
        keyPair.initialize(2048, SecureRandom.getInstanceStrong());
        rsaKeyPair = keyPair.generateKeyPair();

        publicKeyPem = tmpFolder.resolve(Paths.get("test_public.pem"));
        privateKeyPem = tmpFolder.resolve(Paths.get("test_private.pem"));

        writePemFile(publicKeyPem, new X509EncodedKeySpec(rsaKeyPair.getPublic().getEncoded()));
        writePemFile(privateKeyPem, new PKCS8EncodedKeySpec(rsaKeyPair.getPrivate().getEncoded()));
    }

    protected static void writePemFile(final Path path, final EncodedKeySpec encodedKeySpec) throws IOException {
        try (var pemWriter = new PemWriter(Files.newBufferedWriter(path))) {
            final var pemObject = new PemObject("SOME KEY", encodedKeySpec.getEncoded());
            pemWriter.writeObject(pemObject);
            pemWriter.flush();
        }
    }


}
