/* BSD License
 *
 * Copyright (c) 2007, Harald Wellmann, Harman/Becker Automotive Systems
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 * 
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 * 
 *     * Redistributions in binary form must reproduce the above
 *       copyright notice, this list of conditions and the following
 *       disclaimer in the documentation and/or other materials provided
 *       with the distribution.
 * 
 *     * Neither the name of Harman/Becker Automotive Systems 
 *       nor the names of their contributors may be used to
 *       endorse or promote products derived from this software without
 *       specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package datascript.eclipse.editor;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;

public class DataScriptEditorConfiguration extends SourceViewerConfiguration
{
    private DataScriptCodeScanner codeScanner = DataScriptEditorEnvironment
            .getCodeScanner();

    public String[] getConfiguredContentTypes(ISourceViewer sourceViewer)
    {
        return DataScriptPartitionScanner.PARTITION_TYPES;
    }

    public IPresentationReconciler getPresentationReconciler(
            ISourceViewer sourceViewer)
    {
        PresentationReconciler reconciler = new PresentationReconciler();

        DefaultDamagerRepairer dr = new DefaultDamagerRepairer(codeScanner);
        reconciler.setDamager(dr, DataScriptPartitionScanner.STRING);
        reconciler.setRepairer(dr, DataScriptPartitionScanner.STRING);

        dr = new DefaultDamagerRepairer(codeScanner);
        reconciler.setDamager(dr,
                DataScriptPartitionScanner.SINGLE_LINE_COMMENT);
        reconciler.setRepairer(dr,
                DataScriptPartitionScanner.SINGLE_LINE_COMMENT);

        dr = new DefaultDamagerRepairer(codeScanner);
        reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
        reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);

        SingleTokenScanner docCommentScanner = new SingleTokenScanner(
                DataScriptColorConstants.DOC_COMMENT);
        dr = new DefaultDamagerRepairer(docCommentScanner);
        reconciler.setDamager(dr, DataScriptPartitionScanner.DOC);
        reconciler.setRepairer(dr, DataScriptPartitionScanner.DOC);

        SingleTokenScanner multiLineCommentScanner = new SingleTokenScanner(
                DataScriptColorConstants.COMMENT);
        dr = new DefaultDamagerRepairer(multiLineCommentScanner);
        reconciler
                .setDamager(dr, DataScriptPartitionScanner.MULTI_LINE_COMMENT);
        reconciler.setRepairer(dr,
                DataScriptPartitionScanner.MULTI_LINE_COMMENT);

        return reconciler;
    }
}