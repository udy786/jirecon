/*
 * Jirecon, the Jitsi recorder container.
 * 
 * Distributable under LGPL license. See terms of license at gnu.org.
 */

package org.jitsi.jirecon.dtlscontrol;

import java.util.*;
import java.util.Map.Entry;

import org.jitsi.service.configuration.ConfigurationService;
import org.jitsi.service.libjitsi.LibJitsi;
import org.jitsi.service.neomedia.*;

public class JireconDtlsControlManagerImpl
    implements JireconSrtpControlManager
{
    private Map<MediaType, DtlsControl> dtlsControls =
        new HashMap<MediaType, DtlsControl>();

    private String hashFunction;

    private final static String HASH_FUNCTION_KEY = "DTLS_HASH_FUNTION";

    public JireconDtlsControlManagerImpl()
    {
        ConfigurationService configuration = LibJitsi.getConfigurationService();
        hashFunction = configuration.getString(HASH_FUNCTION_KEY);
        MediaService mediaService = LibJitsi.getMediaService();

        for (MediaType mediaType : MediaType.values())
        {
            if (mediaType != MediaType.AUDIO && mediaType != MediaType.VIDEO)
                continue;
            DtlsControl control =
                (DtlsControl) mediaService
                    .createSrtpControl(SrtpControlType.DTLS_SRTP);
            dtlsControls.put(mediaType, control);
            control.setSetup(DtlsControl.Setup.ACTIVE);
        }
    }
    
    @Override
    public void addRemoteFingerprint(MediaType mediaType, String fingerprint)
    {
        final DtlsControl dtlsControl = dtlsControls.get(mediaType);
        final Map<String, String> fingerprints = new HashMap<String, String>();
        fingerprints.put(hashFunction, fingerprint);
        dtlsControl.setRemoteFingerprints(fingerprints);
    }

    @Override
    public String getLocalFingerprint(MediaType mediaType)
    {
        return dtlsControls.get(mediaType).getLocalFingerprint();
    }

    @Override
    public String getLocalFingerprintHashFunction(MediaType mediaType)
    {
        return dtlsControls.get(mediaType).getLocalFingerprintHashFunction();
    }

    @Override
    public SrtpControl getSrtpControl(MediaType mediaType)
    {
        return dtlsControls.get(mediaType);
    }

    @Override
    public Map<MediaType, SrtpControl> getAllSrtpControl()
    {
        Map<MediaType, SrtpControl> controls =
            new HashMap<MediaType, SrtpControl>();
        for (Entry<MediaType, DtlsControl> e : dtlsControls.entrySet())
        {
            controls.put(e.getKey(), e.getValue());
        }
        return controls;
    }
}
